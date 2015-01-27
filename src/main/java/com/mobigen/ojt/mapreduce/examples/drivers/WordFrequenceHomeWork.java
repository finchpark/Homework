package HadoopHW;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordFrequenceHomeWork extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(),
				new WordFrequenceHomeWork(), args);
		System.exit(result);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf, "Word Frequence");

		job.setJarByClass(WordFrequenceHomeWork.class);
		job.setMapperClass(WordFrequenceMapper.class);
		job.setReducerClass(WordFrequenceReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static class WordFrequenceMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		/**
		 * @input: (docID, contents)
		 * @output: ((term, docID), 1)
		 */
		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			FileSplit fileSplit = (FileSplit) context.getInputSplit();
			String docID = fileSplit.getPath().getName();

			Pattern pattern = Pattern.compile("\\w+");
			Matcher matcher = pattern.matcher(value.toString());

			while (matcher.find()) {
				String term = matcher.group().toLowerCase();
				context.write(new Text(term + " " + docID), new IntWritable(1));
			}
		}
	}

	public static class WordFrequenceReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		
		private IntWritable termFreq = new IntWritable();

		/**
		 * @input: ((term, docID), [1,..])
		 * @output: ((term, docID), termFreq)
		 */
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			
			for(IntWritable value : values){
				sum += value.get();
			}
			
			termFreq.set(sum);
			context.write(key, termFreq);
		}
	}
}
