package HadoopHW;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tools.ant.util.TeeOutputStream;

public class WordTFIDFHomeWork extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(),
				new WordTFIDFHomeWork(), args);
		System.exit(result);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fileSystem = FileSystem.get(conf);

		FileStatus[] fileStatus = fileSystem.listStatus(new Path(args[0]));
		conf.setInt("N", fileStatus.length);

		Job job = new Job(conf, "TF-IDF");

		job.setJarByClass(WordTFIDFHomeWork.class);
		job.setMapperClass(WordTFIDFMapper.class);
		job.setReducerClass(WordTFIDFReducer.class);

		job.setOutputKeyClass(TextInputFormat.class);
		job.setOutputValueClass(TeeOutputStream.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		TextInputFormat.addInputPath(job, new Path(args[1]));
		TextOutputFormat.setOutputPath(job, new Path(args[2]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static class WordTFIDFMapper extends
			Mapper<LongWritable, Text, Text, Text> {

		private static int N;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			N = conf.getInt("N", 0);
		}

		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] fields = value.toString().split("\\s+");
			String term = fields[0];
			String docID = fields[1];

			int termFreq = Integer.parseInt(fields[2]);
			int n = Integer.parseInt(fields[3]);

			double idf = Math.log(N / n);
			double tfidf = termFreq * idf;

			context.write(new Text(term + "@" + docID),
					new Text(String.valueOf(tfidf)));
		}

	}

	public static class WordTFIDFReducer extends
			Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context)
				throws IOException, InterruptedException {
			for(Text value : values){
				context.write(key, value);
			}
		}
	}
}
