/*
 * Copyright (c) 2007-2014 Concurrent, Inc. All Rights Reserved.
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

package cascading.flow.planner;

import cascading.flow.FlowException;
import cascading.flow.planner.graph.ElementGraph;
import cascading.flow.planner.iso.finder.GraphFinderException;
import cascading.flow.planner.rule.Rule;
import cascading.pipe.Pipe;
import cascading.util.Util;

/**
 * Class PlannerException is thrown when a job planner fails.
 * <p/>
 * For debugging purposes, the PlannerException holds a copy of the internal job representation which can be
 * written out to disk and visualized with tools that support the
 * <a href="http://en.wikipedia.org/wiki/DOT_language">DOT file format</a> using the {@link #writeDOT(String)}
 * method.
 */
public class PlannerException extends FlowException
  {
  /** Field pipeGraph */
  ElementGraph elementGraph;

  /** Constructor PlannerException creates a new PlannerException instance. */
  public PlannerException()
    {
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param pipe    of type Pipe
   * @param message of type String
   */
  public PlannerException( Pipe pipe, String message )
    {
    super( Util.formatTrace( pipe, message ) );
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param pipe      of type Pipe
   * @param message   of type String
   * @param throwable of type Throwable
   */
  public PlannerException( Pipe pipe, String message, Throwable throwable )
    {
    super( Util.formatTrace( pipe, message ), throwable );
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param pipe         of type Pipe
   * @param message      of type String
   * @param throwable    of type Throwable
   * @param elementGraph of type ElementGraph
   */
  public PlannerException( Pipe pipe, String message, Throwable throwable, FlowElementGraph elementGraph )
    {
    super( Util.formatTrace( pipe, message ), throwable );
    this.elementGraph = elementGraph;
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param string of type String
   */
  public PlannerException( String string )
    {
    super( string );
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param string    of type String
   * @param throwable of type Throwable
   */
  public PlannerException( String string, Throwable throwable )
    {
    super( string, throwable );
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param throwable of type Throwable
   */
  public PlannerException( Throwable throwable )
    {
    super( throwable );
    }

  /**
   * Constructor PlannerException creates a new PlannerException instance.
   *
   * @param string       of type String
   * @param throwable    of type Throwable
   * @param elementGraph of type SimpleDirectedGraph<FlowElement, Scope>
   */
  public PlannerException( String string, Throwable throwable, FlowElementGraph elementGraph )
    {
    super( string, throwable );
    this.elementGraph = elementGraph;
    }

  public PlannerException( Rule rule, Exception exception )
    {
    super( "failed on rule: " + rule.getRuleName(), exception );
    }

  /**
   * Method getElementGraph returns the elementGraph of this PlannerException object.
   *
   * @return the pipeGraph (ElementGraph) of this PlannerException object.
   */
  ElementGraph getElementGraph()
    {
    return elementGraph;
    }

  /**
   * Method writeDOT writes the failed Flow instance to the given filename as a DOT file for import into a graphics package.
   *
   * @param filename of type String
   */
  public void writeDOT( String filename )
    {
    if( elementGraph == null )
      return;

    elementGraph.writeDOT( filename );
    }

  public void writeCauseDOT( String filename )
    {
    if( getCause() instanceof GraphFinderException )
      ( (GraphFinderException) getCause() ).writeDOT( filename );
    }


  }
