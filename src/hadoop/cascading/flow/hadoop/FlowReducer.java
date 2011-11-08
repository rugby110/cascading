/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.flow.hadoop;

import java.io.IOException;
import java.util.Iterator;

import cascading.CascadingException;
import cascading.flow.FlowException;
import cascading.flow.FlowSession;
import cascading.tuple.Tuple;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/** Class FlowReducer is the Hadoop Reducer implementation. */
public class FlowReducer extends MapReduceBase implements Reducer
  {
  /** Field flowReducerStack */
  private HadoopReduceStreamGraph streamGraph;
  /** Field currentProcess */
  private HadoopFlowProcess currentProcess;

  private boolean calledPrepare = false;
  private HadoopGroupGate group;

  /** Constructor FlowReducer creates a new FlowReducer instance. */
  public FlowReducer()
    {
    }

  @Override
  public void configure( JobConf jobConf )
    {
    try
      {
      super.configure( jobConf );
      HadoopUtil.initLog4j( jobConf );
      currentProcess = new HadoopFlowProcess( new FlowSession(), jobConf, false );

      HadoopFlowStep step = (HadoopFlowStep) HadoopUtil.deserializeBase64( jobConf.getRaw( "cascading.flow.step" ) );

      streamGraph = new HadoopReduceStreamGraph( currentProcess, step );

      group = (HadoopGroupGate) streamGraph.getHeads().iterator().next();
      }
    catch( Throwable throwable )
      {
      if( throwable instanceof CascadingException )
        throw (CascadingException) throwable;

      throw new FlowException( "internal error during reducer configuration", throwable );
      }
    }

  public void reduce( Object key, Iterator values, OutputCollector output, Reporter reporter ) throws IOException
    {
    currentProcess.setReporter( reporter );
    currentProcess.setOutputCollector( output );

    if( !calledPrepare )
      {
      streamGraph.prepare();

      calledPrepare = true;

      group.start( group );
      }

    try
      {
      group.run( (Tuple) key, values );
      }
    catch( Throwable throwable )
      {
      if( throwable instanceof CascadingException )
        throw (CascadingException) throwable;

      throw new FlowException( "internal error during reducer execution", throwable );
      }
    }

  @Override
  public void close() throws IOException
    {
    group.complete( group );

    streamGraph.cleanup();

    super.close();
    }
  }
