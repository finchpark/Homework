package com.mobigen.ojt.mapreduce.examples.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountHomeWork extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		
		int result = ToolRunner.run(new Configuration(), new WordCountHomeWork(), args);
		System.exit(result);
	}

	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();
		Job job = new Job(conf, "Counts");
		
		job.setJarByClass(WordCountHomeWork.class);
		job.setMapperClass(WordFrequenceHomeWork.class);
		job.setReducerClass(WordTFIDFHomeWork.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static class WordFrequenceHomeWork extends Mapper<LongWritable, Text, Text, Text>{
		
	}
	
	public static class WordTFIDFHomeWork extends Reducer<Text, Text, Text, Text>{
		
	}
}
