/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.dbutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ScriptUtil
{

    public static void main(String[] args)
    {
        System.out.println("Running DBUtils " + args[0]);
        try
        {
            if(args.length == 2 && args[0].equals("-drops"))
            {
                dropdrops(args[1]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void dropdrops(String filename)
    throws Exception
    {
        System.out.println("dropping drops from " + filename);
        String outputFileName = filename + ".tmp";
        FileReader fr = new FileReader(filename);
        BufferedReader reader = new BufferedReader(fr);
        
        FileWriter fw = new FileWriter(outputFileName);
        PrintWriter writer = new PrintWriter(fw); 
        String line;
        while ((line = reader.readLine()) != null)
        {
            String temp = line.toUpperCase();
            if (!temp.startsWith("DROP"))
            {
                writer.println(line);
            }
        }
        fr.close();
        fw.close();
        File original = new File(filename);
        File modified = new File(outputFileName);
        original.delete();
        modified.renameTo(original);
    }
    
}