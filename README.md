# esBench

![alt text](https://travis-ci.org/kucera-jan-cz/esBench.svg?branch=master "Logo Title Text 1")

**esBench** is performance and unit test tool for generating Elasticsearch documents based on existing data. 
The common use cases are:
- **customer issues** - thanks to esBench developer can ask customer for creating "workload" (metadata automatically created by esBench) from their production Elasticsearch and recreate similar environment with similar data on his development machine
- **performance testing** - esBench can fastly generate JSON documents so that you can test your Elasticsearch indexing/searching performance. On top of that tool offers ability to send documents from multiple nodes (clustered insert)
- **disk space test** - esBench can generate milions of documents to testing Elasticsearch cluster based on existing indices and give you rought idea how much disk space you will need to maintain big data cluster

### Example usage
````
esbench collect --index=games --type=board_games
esbench insert --index=perf_games --type=game --insert.docs=10000 --insert.threads=8
````

## Getting Started
1. Download latest release of esBench
```
 wget https://github.com/kucera-jan-cz/esBench/releases/download/v0.0.2-alpha/esbech-0.0.2-alpha.zip
 unzip esbech-0.0.2-alpha.zip
 cd esbech-0.0.2-SNAPSHOT/
```
2. Insert testing documents to your localhost Elasticsearch cluster named myCluster
```
./bin/esbench.sh insert --cluster.name=myCluster --index=board_games --type=game --insert.docs=1000 --workload=examples/board_games.json
```
3. Collect workload (esBench metadata about existing index) from your localhost ElasticSearch named myCluster to file my_workload.json
```
./bin/esbench.sh collect --cluster.name=myCluster --index=board_games --type=game --workload=workloads/my_workload.json
```

### Requirements:
* Java 8
* Elasticsearch 2.0+ [Older versions may be supported]
 
### Development and feedback
Currently **esBench** in **early phase of development** meaning that API, configuration format may change release to release. Early adopters are **more than welcome** to provide feedback and influence [future versions and features](https://github.com/kucera-jan-cz/esBench/wiki/Future-features).
