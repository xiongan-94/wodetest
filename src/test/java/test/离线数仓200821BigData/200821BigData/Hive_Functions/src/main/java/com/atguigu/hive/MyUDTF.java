package com.atguigu.hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VULCAN on 2020/12/9
 */
public class MyUDTF extends GenericUDTF {

    /*
                1.告诉Hive函数的入参类型
                        输入： [{abc},{bcd}]

                        StructObjectInspector argOIs： 输入的参数的所有的类型结构！
                2.告诉Hive函数输出的每行的ObjectInspector类型

     */

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {

        // 获取输入的所有的字段类型
        List<? extends StructField> inputFields = argOIs.getAllStructFieldRefs();

        // 检查是否传入是一行一列
        if (inputFields.size() != 1){
            throw new UDFArgumentException("此函数只能允许传入一列!");
        }

        //检查传入的数据类型是否是string类型
        if (!"string".equals(inputFields.get(0).getFieldObjectInspector().getTypeName())){
            throw new UDFArgumentException("此函数只允许传入string类型!");
        }

        //告诉Hive函数输出的每行的ObjectInspector类型

        //放入输出的一行的字段名
        List<String> fieldNames = new ArrayList<>();

        fieldNames.add("actionJsonObject");

        //放入输出的一行的字段类型
        List<ObjectInspector> fieldOIs=new ArrayList<>();

        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    // 输出是一行一列
    private String [] result=new String[1];

    // 处理输入的记录，返回结果     [{abc},{bcd}]
    @Override
    public void process(Object[] args) throws HiveException {

        //将JSONArray 字符串，转为JsonArray对象
        JSONArray jsonArray = new JSONArray(args[0].toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            //将要输出的每个结果放入模具中
            result[0]=jsonArray.getJSONObject(i).toString();
            //输出
            forward(result);
        }

    }

    @Override
    public void close() throws HiveException {

    }
}
