/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tstool.mr;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * @author kamir
 */
public class TSCReduce  extends Reducer<Text, IntWritable, Text, IntWritable> {

        protected void reduce(Text key, Iterable<IntWritable> values, Reducer.Context context) throws IOException, InterruptedException {

            int sum = 0;

            while (values.iterator().hasNext()) {
                  sum += values.iterator().next().get();
            }
            
            context.write(key, new IntWritable(sum));
        }

    }