# esBench

![alt text](https://travis-ci.org/kucera-jan-cz/esBench.svg?branch=master "Logo Title Text 1")

esBench is performance and unit test tool for generating ElasticSearch documents based on existing data. 

##Example format of configuration
```
{
    "version" : "0.1",
    //Defaults works as templating for fields to minimize verbosity of configuration
    "defaults" : {
        "BOOLEAN":{"type":"BOOLEAN", "array":1, "strategy":"SEQUENCE", "tokens":"TICK_TOCK"},
        "STRING":{"type":"STRING", "array":1, "words":1, "strategy":"SEQUENCE", "tokens":[ ]},
        "INTEGER":{"type":"INTEGER", "array":1, "strategy":"SEQUENCE", "from": 1, "step": 1, "to":100},
    },
    "histogram" : [ {
      "index" : "",
      "type" : "",
      "fields" : {
        "name" : {"type":"STRING", "words" : 2, "tokens" : ["Risk", "Cluedo", "Arkham", "Horror", "Scrabble"]},
        "maxPlayers":{"type":"LONG", "to":2000},
        "minPlayers":{"type":"LONG"},
        "categories":{"type":"STRING", "words":2, "tokens":[ "Abstract Strategy", "Action / Dexterity", "Adventure", "Age of Reason", "American Civil War"]
      }
    } ]
}
```
#License
```
This software is licensed under the Apache License, version 2 ("ALv2"), quoted below.

Copyright 2015 esBench <https://github.com/kucera-jan-cz/esBench/wiki>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
