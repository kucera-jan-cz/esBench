# esBench

![alt text](https://travis-ci.org/kucera-jan-cz/esBench.svg?branch=master "Logo Title Text 1")

**esBench** is performance and unit test tool for generating Elasticsearch documents based on existing data. 
The common use cases are:
- **customer issues** - thanks to esBench developer can ask customer for creating "workload" (metadata for esBench) and recreate similar environment with similar data on his development machine
- **performance testing** - esBench can fastly generate JSON documents so that you can test your Elasticsearch indexing/searching performance
- **disk space test** - esBench can generate milions of documents to testing Elasticsearch cluster based on existing indices and give you rought idea how much disk space you will need to maintain big data cluster

### Example usage
````
esbench collect --index=games --type=board_games
esbench insert --index=perf_games --type=game --insert.docs=10000 --insert.threads=8
````

### Requirements:
* Java 8
* Elasticsearch 2.0+ [Older versions may be supported]