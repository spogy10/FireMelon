package com.jr.poliv.firemelon.Stack;

import android.util.Log;

import java.io.File;
import java.util.Stack;

/**
 * Created by poliv on 8/20/2017.
 */

public class BackStack extends Stack<File> {


    public BackStack() {
        super();
    }

    public BackStack(String filePath){
        super();
        if(filePath.startsWith(File.separator+"storage"+File.separator+"emulated"+File.separator+"0"))
            getStackTraceEmulated(filePath);
        else
            getStackTrace(filePath);
    }

    private void getStackTraceEmulated(String filePath){

        if(filePath.equals(File.separator+"storage"+File.separator+"emulated"+File.separator+"0")){
            push(new File(File.separator+"storage"+File.separator));
            push(new File(File.separator+"storage"+File.separator+"emulated"+File.separator+"0"));
        }else{

            getStackTraceEmulated(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            push(new File(filePath));
        }

    }

    private void getStackTrace(String filePath){

        if((!filePath.equals("")) || (filePath != null)){
            if(filePath.equals(File.separator+"storage")){
                push(new File(File.separator+"storage"+File.separator));
            }else{

                getStackTrace(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                push(new File(filePath));
            }
        }
    }

    public int stackCount(){
        int count = 0;
        if(!isEmpty()){
            BackStack tempStack = new BackStack();

            while(!isEmpty()){
                File tempFile = this.pop();
                Log.d("Paul", "File Path:"+tempFile.getAbsolutePath());
                tempStack.push(tempFile);
                count++;
            }
            while(!tempStack.isEmpty())
                this.push(tempStack.pop());

        }else{
            System.out.println("Stack is empty");
        }
        Log.d("Paul", "Stack count: "+count);
        return count;
    }
}
