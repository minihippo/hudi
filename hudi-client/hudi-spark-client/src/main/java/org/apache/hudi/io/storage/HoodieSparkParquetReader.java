/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.io.storage;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hudi.common.bloom.BloomFilter;
import org.apache.hudi.common.model.HoodieFileFormat;
import org.apache.hudi.common.model.HoodieRecord;
import org.apache.hudi.common.util.BaseFileUtils;
import org.apache.hudi.common.util.ClosableIterator;
import org.apache.hudi.common.util.MappingIterator;
import org.apache.hudi.common.util.ParquetReaderIterator;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetRecordReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.execution.datasources.parquet.ParquetReadSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HoodieSparkParquetReader implements HoodieSparkFileReader {

  private final Path path;
  private final Configuration conf;
  private final BaseFileUtils parquetUtils;
  private List<ParquetReaderIterator> readerIterators = new ArrayList<>();

  public HoodieSparkParquetReader(Configuration conf, Path path) {
    this.path = path;
    this.conf = conf;
    this.parquetUtils = BaseFileUtils.getInstance(HoodieFileFormat.PARQUET);
  }

  @Override
  public String[] readMinMaxRecordKeys() {
    return parquetUtils.readMinMaxRecordKeys(conf, path);
  }

  @Override
  public BloomFilter readBloomFilter() {
    return parquetUtils.readBloomFilterFromMetadata(conf, path);
  }

  @Override
  public Set<String> filterRowKeys(Set<String> candidateRowKeys) {
    return parquetUtils.filterRowKeys(conf, path, candidateRowKeys);
  }

  @Override
  public ClosableIterator<HoodieRecord> getRecordIterator(Schema readerSchema, HoodieRecord.Mapper mapper) throws IOException {
    ReadSupport readSupport = new ParquetReadSupport();
    ParquetReader reader = ParquetReader.<InternalRow>builder(readSupport, path).withConf(conf).build();
    ParquetReaderIterator<InternalRow> parquetReaderIterator = new ParquetReaderIterator<>(reader);
    readerIterators.add(parquetReaderIterator);
    // TODO: mapper to support internal row
    // return new MappingIterator<>(parquetReaderIterator, mapper::apply);
    return null;
  }

  @Override
  public Schema getSchema() {
    return parquetUtils.readAvroSchema(conf, path);
  }

  @Override
  public void close() {
    readerIterators.forEach(ParquetReaderIterator::close);
  }

  @Override
  public long getTotalRecords() {
    return parquetUtils.getRowCount(conf, path);
  }
}
