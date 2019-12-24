package edu.zju.gis.hls.trajectory.datastore.storage.writer.mongo;

import edu.zju.gis.hls.trajectory.datastore.storage.writer.LayerWriterConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hu
 * @date 2019/12/19
 **/
@Getter
@Setter
@AllArgsConstructor
public class MongoLayerWriterConfig extends LayerWriterConfig {
  private String uri;
  private String database;
  private String collection;
}
