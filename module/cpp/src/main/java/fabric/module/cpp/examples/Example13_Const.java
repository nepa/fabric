/**
 * Copyright (c) 2010, Dennis Pfisterer, Marco Wegner, Institute of Telematics, University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package fabric.module.cpp.examples;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.c.CFun;
import de.uniluebeck.sourcegen.c.Cpp;
import de.uniluebeck.sourcegen.c.CppClass;
import de.uniluebeck.sourcegen.c.CppFun;
import de.uniluebeck.sourcegen.c.CppSourceFile;
import de.uniluebeck.sourcegen.c.CppVar;

/**
 * This is a basic class to generate other examples
 *
 * - Compile with: g++ Const.cpp -o const
 * - Run with: ./const
 * - Returns:
 *
 * @see http://www.cprogramming.com/tutorial/statickeyword.html for the example
 * @author Dennis Boldt
 *
 */

public class Example13_Const {

	private Workspace workspace = null;

	public Example13_Const(Workspace workspace) throws Exception {
	    this.workspace = workspace;
		generate();
	}

	void generate() throws Exception {

		String className = "Const";

        // Generate the class -- without an explicit file
        CppClass clazz = CppClass.factory.create(className);

        // Add variables
        CppVar version = CppVar.factory.create(Cpp.PRIVATE | Cpp.CONST, "int", "version", "10");
        clazz.add(version);

        // Add function
        CppFun print = CppFun.factory.create(Cpp.STATIC ^ Cpp.INT, "print");
        print.appendCode("cout << \"Version \" << " + version.getVarName() + " << \"\\n\";");
        clazz.add(Cpp.PUBLIC, print);


        // Generate the file
		CppSourceFile file = workspace.getC().getCppSourceFile(className);
        file.addLibInclude("iostream");
        file.addUsingNamespace("std");

        // Add the main function to the file
        CFun fun_main = CFun.factory.create("main", "int", null);
        fun_main.appendCode(className + " c;");
        fun_main.appendCode("c.print();");

        file.add(fun_main);

        // Finally, add class to the file
        file.add(clazz);
	}
}
