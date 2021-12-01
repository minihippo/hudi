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

package org.apache.hudi.utils;

import java.util.List;

public class HiveHash extends HashFunction {
  @Override
  public String toString() {
    return HiveHash.class.toString();
  }

  @Override
  public int hash(Object value) {
    if (value == null) {
      return 0;
    } else if (value instanceof List) {
      int result = 0;
      int i = 0;
      List list = (List) value;
      while (i < list.size()) {
        result = (31 * result) + hash(list.get(i));
        i++;
      }
      return result;
    } else if (value instanceof Boolean) {
      return value.equals(Boolean.TRUE) ? 1 : 0;
    } else {
      return value.hashCode();
    }
  }
}
