package edu.zju.gis.hls.trajectory.analysis.index.rtree;

import edu.zju.gis.hls.trajectory.analysis.index.SpatialIndex;
import edu.zju.gis.hls.trajectory.analysis.index.quadtree.QuadTreeIndex;
import edu.zju.gis.hls.trajectory.analysis.index.quadtree.QuadTreeIndexLayer;
import edu.zju.gis.hls.trajectory.analysis.model.Feature;
import edu.zju.gis.hls.trajectory.analysis.model.FeatureType;
import edu.zju.gis.hls.trajectory.analysis.rddLayer.Layer;
import edu.zju.gis.hls.trajectory.analysis.rddLayer.LayerType;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;
import scala.Tuple2;

import java.util.*;

/**
 * @author Hu
 * @date 2019/12/24
 * 每个partition内部的R树索引
 **/
public class RTreeIndex implements SpatialIndex, Serializable {

  private static final Logger logger = LoggerFactory.getLogger(RTreeIndex.class);
  private QuadTreeIndex qti;

  public RTreeIndex() {
    this.qti = new QuadTreeIndex();
  }

  public RTreeIndex(int z) {
    this.qti = new QuadTreeIndex(z);
  }

  /**
   * 在每个分片内部构建R-Tree索引
   * @param layer
   * @return
   */
  @Override
  public RTreeIndexLayer index(Layer layer) {
    return this.indexRTree(this.indexQuadTree(layer));
  }

  private QuadTreeIndexLayer indexQuadTree(Layer layer) {
    logger.info("Construct quad tree index first");
    return (QuadTreeIndexLayer) qti.index(layer);
  }

  private RTreeIndexLayer indexRTree(QuadTreeIndexLayer layer) {
    Layer l = layer.getLayer();
    RTreeIndexLayer result = new RTreeIndexLayer();
    result.setLayer(layer);
    result.setIndexedPartition(l.mapPartitionsToPair(new RTreeIndexBuilder()));
    return result;
  }

  @Getter
  @Setter
  private class RTreeIndexBuilder<V extends Feature> implements PairFlatMapFunction<Iterator<Tuple2<String, V>>, String, RTree> {

    @Override
    public Iterator<Tuple2<String, RTree>> call(Iterator<Tuple2<String, V>> t) throws Exception {

      List<Tuple2<String, RTree>> result = new ArrayList<>();
      String gridId;
      RTree rTree;
      if (t.hasNext()) {
        Tuple2<String, V> m = t.next();
        gridId = m._1;
        Class c = m._2.getClass();
        LayerType lt = LayerType.findLayerType(FeatureType.getType(c.getName()));
        rTree = new RTree(lt);
        rTree.insert(m._2.getGeometry().getEnvelopeInternal(), m._2);
      } else {
        return result.iterator();
      }

      while(t.hasNext()) {
        Tuple2<String, V> m = t.next();
        rTree.insert(m._2.getGeometry().getEnvelopeInternal(), m._2);
      }
      result.add(new Tuple2<>(gridId, rTree));
      return result.iterator();
    }
  }

}