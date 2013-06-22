Hadoop.TS
=========

A collection of tools for time series processing in Hadoop.

Hadoop.TS is an open source platform for large-scale time-series processing on top
of Apache Hadoop.

It consists of a set of simple generic classes to represent and transform complex data structures, 
stored in HDFS or HBase.

The core components are: TimeSeries, TSBuckets and TSProcessors. Multiple submodules contain implementations
of specific algorithms for univariate or bivariate time series ananlysis, especially for applications in
complex systems research. 

A major aspects of Hadoop.TS is to enable rapid prototyping for new algorithms based on existing
preprocessed data, stored in Hadoop clusters. Deployment and developement cycles are short, because
all algorithms will come with demo applications, e.g:
- extraction of time series data from logfiles like Wikipedia click count data 
- connection to intermediate data sources like Nutch crawl results or Lucene index files.
- conversion between time series and inter-event-time time-series.
- generating of standard output for network analysis tools like Gephi, or networkx
- integration into Mahout libraries, based on data which is stored in SequenceFiles readable for Mahout

In this sense Hadoop.TS serves as a 'large-scale glueware' for existing resources. As it is Hadoop-based, 
the Hadoop.TS project benefits from all it's nice features like fault-tolerance and scalability. 

WIKI : https://github.com/kamir/Hadoop.TS/wiki
------
