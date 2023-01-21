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

/**
 *  Metaserver storage Initialization that create tables defined in the DDLMappper.
 */
package org.apache.hudi.metaserver;

import org.apache.hudi.exception.HoodieException;
import org.apache.hudi.metaserver.store.MetaserverStorage;
import org.apache.hudi.metaserver.store.RelationalDBBasedStorage;
import org.apache.hudi.metaserver.thrift.MetaserverStorageException;

public class HoodieMetaserverPreparations {

  public static void main(String[] args) {
    MetaserverStorage metaserverStorage = new RelationalDBBasedStorage();
    try {
      metaserverStorage.initStorage();
    } catch (MetaserverStorageException e) {
      throw new HoodieException("Fail to init the Metaserver's storage." + e);
    }
  }
}
