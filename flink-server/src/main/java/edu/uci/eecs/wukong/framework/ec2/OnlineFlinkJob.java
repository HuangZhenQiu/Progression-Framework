package edu.uci.eecs.wukong.framework.ec2;

/**
 * The flink job that consume data from amazon-kinesis cluster. It does the same activity classification as
 * FlinkServer. The difference is that it run as a distributed job, and it uses session window for each host
 * key.
 *
 */
public class OnlineFlinkJob {
}
