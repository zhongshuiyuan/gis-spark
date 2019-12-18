package edu.zju.gis.hls.trajectory.analysis.rddLayer;

import edu.zju.gis.hls.trajectory.analysis.model.Feature;
import edu.zju.gis.hls.trajectory.analysis.model.Term;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @author Hu
 * @date 2019/9/21
 **/
@Getter
@Setter
@ToString
public class LayerMetadata extends Feature<Polygon> {

  private String layerId;
  private String layerName;
  private CoordinateReferenceSystem crs = Term.DEFAULT_CRS;

  public LayerMetadata(String fid, Polygon geometry, Map<String, Object> attributes, String layerId, String layerName, CoordinateReferenceSystem crs) {
    super(fid, geometry, attributes);
    this.layerId = layerId;
    this.layerName = layerName;
    this.crs = crs;
  }

  public LayerMetadata() {
    this.layerId = UUID.randomUUID().toString();
    this.layerName = this.layerId;
  }

  public LayerMetadata(LayerMetadata metadata) {
    this.layerId = metadata.layerId;
    this.layerName = metadata.layerName;
  }

  public Envelope getExtent() {
    return this.geometry.getEnvelopeInternal();
  }

}
