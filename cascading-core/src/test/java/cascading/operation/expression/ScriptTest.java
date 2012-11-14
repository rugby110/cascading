/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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

package cascading.operation.expression;

import cascading.CascadingTestCase;
import cascading.flow.FlowProcess;
import cascading.operation.ConcreteCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

/**
 *
 */
public class ScriptTest extends CascadingTestCase
  {
  public ScriptTest()
    {
    }

  public void testSimpleScript()
    {
    Class returnType = Long.class;
    String[] names = new String[]{"a", "b"};
    Class[] types = new Class[]{long.class, int.class};

    assertEquals( 3l, evaluate( "return a + b;", returnType, names, types, getEntry( 1, 2 ) ) );
    assertEquals( 3l, evaluate( "return a + b;", returnType, names, types, getEntry( 1.0, 2.0 ) ) );
    assertEquals( 3l, evaluate( "return a + b;", returnType, names, types, getEntry( "1", 2.0 ) ) );

    returnType = Double.class;
    types = new Class[]{double.class, int.class};

    assertEquals( 3d, evaluate( "return a + b;", returnType, names, types, getEntry( 1, 2 ) ) );
    assertEquals( 3d, evaluate( "return a + b;", returnType, names, types, getEntry( 1.0, 2.0 ) ) );
    assertEquals( 3d, evaluate( "return a + b;", returnType, names, types, getEntry( "1", 2.0 ) ) );

    returnType = Boolean.TYPE;
    names = new String[]{"a", "b"};
    types = new Class[]{String.class, int.class};
    assertEquals( true, evaluate( "return (a != null) && (b > 0);", returnType, names, types, getEntry( "1", 2.0 ) ) );

    names = new String[]{"$0", "$1"};
    types = new Class[]{String.class, int.class};
    assertEquals( true, evaluate( "return ($0 != null) && ($1 > 0);", returnType, names, types, getEntry( "1", 2.0 ) ) );

    names = new String[]{"a", "b", "c"};
    types = new Class[]{float.class, String.class, String.class};
    assertEquals( true, evaluate( "return b.equals(\"1\") && (a == 2.0) && c.equals(\"2\");", returnType, names, types, getEntry( 2.0, "1", "2" ) ) );

    names = new String[]{"a", "b", "$2"};
    types = new Class[]{float.class, String.class, String.class};
    assertEquals( true, evaluate( "return b.equals(\"1\") && (a == 2.0) && $2.equals(\"2\");", returnType, names, types, getEntry( 2.0, "1", "2" ) ) );

    String script = "";
    script += "boolean first = b.equals(\"1\");\n";
    script += "return first && (a == 2.0) && $2.equals(\"2\");\n";
    assertEquals( true, evaluate( script, returnType, names, types, getEntry( 2.0, "1", "2" ) ) );
    }

  private Object evaluate( String expression, Class returnType, String[] names, Class[] types, TupleEntry tupleEntry )
    {
    ScriptFunction function = getFunction( expression, returnType, names, types );

    ConcreteCall<ExpressionOperation.Context> call = new ConcreteCall<ExpressionOperation.Context>();
    function.prepare( FlowProcess.NULL, call );

    return function.evaluate( call.getContext(), tupleEntry );
    }

  private ScriptFunction getFunction( String expression, Class returnType, String[] names, Class[] classes )
    {
    return new ScriptFunction( new Fields( "result" ), expression, returnType, names, classes );
    }

  private TupleEntry getEntry( Comparable lhs, Comparable rhs )
    {
    Fields fields = new Fields( "a", "b" );
    Tuple parameters = new Tuple( lhs, rhs );

    return new TupleEntry( fields, parameters );
    }

  private TupleEntry getEntry( Comparable f, Comparable s, Comparable t )
    {
    Fields fields = new Fields( "a", "b", "c" );
    Tuple parameters = new Tuple( f, s, t );

    return new TupleEntry( fields, parameters );
    }
  }