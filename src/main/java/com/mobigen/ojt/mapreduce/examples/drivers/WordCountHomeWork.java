package com.mobigen.ojt.mapreduce.examples.drivers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		Job job = new Job(conf, "Counts Word");
		
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
		
		/**
		 * @input: ((term, docID), textFrequ)
		 * @output: (term, (docID, textFrequ, 1))
		 */
		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] input = value.toString().split("\\s+");
			String term = input[0];
			String docID = input[1];
			int textFrequ = Integer.parseInt(input[2]);
			
			context.write(new Text(term), new Text(docID + " " + textFrequ));
		}		
	}
	
	public static class WordTFIDFHomeWork extends Reducer<Text, Text, Text, Text>{

		/**
		 * @input (term, [(docID, textFreq, 1), ..])
		 * @output ((term, docID), (textFreq, n))
		 */
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context)
				throws IOException, InterruptedException {
			Map<Text, String> counter = new HashMap<Text, String>();
			int n = 0;
			
			for(Text value : values){
				String[] docID_textFre = value.toString().split("\\s+");
				String docID = docID_textFre[0];
				String textFreq = docID_textFre[1];
				
				counter.put(new Text(key.toString() + " " + docID), textFreq);
				n++;
			}
			
			for(Text term_docID : counter.keySet()){
				String textFreq = counter.get(term_docID);
				context.write(term_docID, new Text(textFreq + " " + n));
			}
		}		
	}
}
