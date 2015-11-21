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
