// Catalano IO Library
// The Catalano Framework
//
// Copyright © Diego Catalano, 2015
// diego.catalano at live.com
//
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//

package Catalano.IO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CSV Parser, handle csv files.
 * @author Diego Catalano
 */
public class CSVParser {
    
    private char delimiter = ',';
    private int startRow = 0;
    private int startCol = 0;
    
    String charset = "UTF-8";
    String newLine = System.getProperty("line.separator");

    /**
     * Get the delimiter.
     * @return Delimiter.
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Set the delimiter.
     * @param delimiter Delimiter.
     */
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Get new line separator.
     * @return New line separator.
     */
    public String getNewLine() {
        return newLine;
    }

    /**
     * Set new line separator.
     * @param newLine New line separator.
     */
    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }

    /**
     * Initialize a new instance of the CSVParser class.
     */
    public CSVParser() {}
    
    /**
     * Initialize a new instance of the Blur class.
     * @param delimiter Delimiter.
     */
    public CSVParser(char delimiter) {
        this.delimiter = delimiter;
    }
    
    /**
     * Initialize a new instance of the Blur class.
     * @param delimiter Delimiter.
     * @param startRow Start row.
     * @param startColumn Start column.
     */
    public CSVParser(char delimiter, int startRow, int startColumn){
        this.delimiter = delimiter;
        this.startRow = startRow;
        this.startCol = startColumn;
    }
    
    /**
     * Read CSV and convert to object type.
     * The method find the constructor that contains the maximum parameters.
     * @param <T> Object type.
     * @param filename CSV filename.
     * @param clazz Object type.
     * @return List of user definied type.
     */
    public <T> List<T> Read(String filename, Class clazz){
        String[][] data = Read(filename);
        return toObject(data, clazz);
    }
    
    /**
     * Read CSV.
     * @param filename Filename.
     * @return CSV table of string.
     */
    public String[][] Read(String filename){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));
            
            String line;
            List<String> lines = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            
            if(lines.size() > 0) {
                String[] temp = lines.get(startRow).split(String.valueOf(delimiter));
                String[][] data = new String[lines.size() - startRow][temp.length - startCol];
                int idxI = 0;
                for (int i = startRow; i < lines.size(); i++) {
                    int idxJ = 0;
                    temp = lines.get(i).split(String.valueOf(delimiter));
                    for (int j = startCol; j < temp.length; j++) {
                        data[idxI][idxJ] = temp[j];
                        idxJ++;
                    }
                    idxI++;
                }

                return data;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public double[][] ReadAsDouble(String filename){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));
            
            String line;
            List<String> lines = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            
            if(lines.size() > 0) {
                String[] temp = lines.get(startRow).split(String.valueOf(delimiter));
                double[][] data = new double[lines.size() - startRow][temp.length - startCol];
                int idxI = 0;
                for (int i = startRow; i < lines.size(); i++) {
                    int idxJ = 0;
                    temp = lines.get(i).split(String.valueOf(delimiter));
                    for (int j = startCol; j < temp.length; j++) {
                        data[idxI][idxJ] = Double.valueOf(temp[j]);
                        idxJ++;
                    }
                    idxI++;
                }

                return data;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     * Convert the CSV string to user definied type.
     * The method find the constructor that contains the maximum parameters.
     * @param <T> Object type.
     * @param data CSV data.
     * @param cls Object type.
     * @return List of user definied type.
     */
    private <T> List<T> toObject(String[][] data, Class cls){
        
        int p = 0;
        Class[] paramTypes = null;
        
        for (Constructor con : cls.getConstructors()) {
            if(con.getParameterCount() > p){
                p = con.getParameterCount();
                paramTypes = con.getParameterTypes();
            }
        }
        
        List<T> lst = new ArrayList<T>(data.length);
       
        try{
            for (int i = 0; i < data.length - startRow - 1; i++) {
                Constructor c = cls.getConstructor(paramTypes);
                Object[] obj = new Object[paramTypes.length];
                for (int j = 0; j < paramTypes.length; j++) {
                    if(paramTypes[j] == int.class){
                        obj[j] = Integer.parseInt(data[i][j]);
                    }
                    else if(paramTypes[j] == boolean.class){
                        obj[j] = Boolean.parseBoolean(data[i][j]);
                    }
                    else if(paramTypes[j] == double.class){
                        obj[j] = Double.parseDouble(data[i][j]);
                    }
                    else if(paramTypes[j] == float.class){
                        obj[j] = Float.parseFloat(data[i][j]);
                    }
                    else if(paramTypes[j] == String.class){
                        obj[j] = data[i][j];
                    }
                }
                
                lst.add((T) c.newInstance(obj));
            }
            return lst;
        }
        catch (NoSuchMethodException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SecurityException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Write the matrix of string in csv file format.
     * @param data Data.
     * @param filename Filename.
     */
    public void Write(String[][] data, String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            
            boolean isLastCol;
            for (int i = 0; i < data.length; i++) {
                isLastCol = false;
                for (int j = 0; j < data[0].length; j++) {
                    if(j==data[0].length-1) isLastCol = true;
                    if(isLastCol)
                        fw.append(data[i][j]);
                    else
                        fw.append(data[i][j] + delimiter);
                }
                fw.append(newLine);
            }
            
            fw.flush();
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Write the matrix of integer in csv file format.
     * @param data Data.
     * @param filename Filename.
     */
    public void Write(int[][] data, String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            
            boolean isLastCol;
            for (int i = 0; i < data.length; i++) {
                isLastCol = false;
                for (int j = 0; j < data[0].length; j++) {
                    if(j==data[0].length-1) isLastCol = true;
                    if(isLastCol)
                        fw.append(String.valueOf(data[i][j]));
                    else
                        fw.append(String.valueOf(data[i][j] + delimiter));
                }
                fw.append(newLine);
            }
            
            fw.flush();
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Write the matrix of integer in csv file format.
     * @param data Data.
     * @param filename Filename.
     */
    public void Write(double[][] data, String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            
            boolean isLastCol;
            for (int i = 0; i < data.length; i++) {
                isLastCol = false;
                for (int j = 0; j < data[0].length; j++) {
                    if(j==data[0].length-1) isLastCol = true;
                    if(isLastCol)
                        fw.append(String.valueOf(data[i][j]));
                    else
                        fw.append(String.valueOf(data[i][j] + delimiter));
                }
                fw.append(newLine);
            }
            
            fw.flush();
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}