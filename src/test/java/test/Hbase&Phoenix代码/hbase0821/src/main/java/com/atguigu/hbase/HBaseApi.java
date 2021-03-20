package com.atguigu.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 1、Namespace相关
 * 		1、创建Namespace: create_namespace '命名空间名称'
 * 		2、查看Namespace信息: describe_namespace '命名空间的名称'
 * 		3、查看所有的Namespace: list_namespace
 * 		4、删除Namespace: drop_namespace '命名空间的名称' [删除命名空间的时候命名空间下不能有表]
 * 		5、修改Namespace: alter_namespace '命名空间的名称',{属性名=属性值}
 * 		6、查看命名空间下所有表:list_namespace_tables '命名空间的名称'
 * 	2、表相关
 * 		1、创建表: create '表名','列簇名1','列簇名2'
 * 		2、创建表，指定列簇的版本号: create '表名',{NAME=>'列簇名',VERSIONS=>'版本数'}
 * 		3、修改表[修改版本]: alter '表名',{NAME=>'列簇名',VERSIONS=>'版本数'}
 * 		4、删除表:
 * 			1、禁用表: disable '表名'
 * 			2、删除表: drop '表名'
 * 	3、数据相关
 * 		1、插入数据: put '表名','rowkey','列簇名:列限定符',值
 * 		2、修改数据: put '表名','rowkey','列簇名:列限定符',值
 * 		3、查询数据：
 * 			1、get查询[get只能根据rowkey查询]
 * 				1、查询整行数据: get '表名','rowkey'
 * 				2、查询某个列簇的数据: get '表名','rowkey','列簇名'
 * 				3、查询某个列的数据: get '表名','rowkey','列簇名:列限定符'
 * 				4、查询多个版本的数据: get '表名','rowkey',{COLUMNS=>'列簇名:列限定符',VERSIONS=>'版本数'}
 * 				5、查询某个时间戳的数据: get '表名','rowkey',{COLUMNS=>'列簇名:列限定符',TIMESTAMP=>时间戳}
 * 			2、scan查询[scan不能根据rowkey查询]
 * 				1、查询整表数据: scan '表名'
 * 				2、查询某个列簇的数据: scan '表名',{COLUMNS=>'列簇名'}
 * 				3、查询某个列的数据：scan '表名',{COLUMNS=>'列簇名:列限定符'}
 * 				4、查询多个版本数据: scan '表名',{VERSIONS=>'版本数'}
 * 				5、查询某个rowkey范围段的数据: scan '表名',{STARTROW=>'起始rowkey',STOPROW=>'结束rowkey'} [查询结果不包含STOPROW]
 * 				6、查询某个时间戳范围段的数据: scan '表名',{TIMERANGE=>[时间戳1,时间戳2]}
 * 		4、删除数据
 * 			1、delete[删除cell数据]
 * 				delete '表','rowkey','列簇:列限定符'
 * 			2、deleteall[删除整行数据、cell数据]
 * 				1、删除整行数据: deleteall '表','rowkey'
 * 				2、删除cell数据: deleteall '表','rowkey','列簇:列限定符'
 * 		5、统计表的行数: count '表名'
 * 		6、清空表数据: truncate '表名'
 */
public class HBaseApi {

    private  Connection connection;

    private Admin admin;

    @Before
    public void init() throws Exception{
        //1、创建hbase connect
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum","hadoop102:2181,hadoop103:2181,hadoop104:2181");

        connection = ConnectionFactory.createConnection(conf);
        //2、创建Admin
        admin = connection.getAdmin();
    }

    /**
     * 创建命名空间
     * @throws Exception
     */
    @Test
    public  void createNamepspace() throws Exception {

        //3、描述namespace
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create("bigdata").build();
        //4、创建
        admin.createNamespace(namespaceDescriptor);

    }

    /**
     * 查看所有的命名空间
     * @throws Exception
     */
    @Test
    public void listNamespace() throws Exception{

        NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();

        for( NamespaceDescriptor desc: namespaceDescriptors) {
            System.out.println( desc.getName());
        }
    }

    /**
     * 修改命名空间
     * @throws Exception
     */
    @Test
    public void alterNamespace() throws Exception{

        //描述namespace
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create("bigdata").addConfiguration("aa", "bb").build();
        admin.modifyNamespace(namespaceDescriptor);
    }

    /**
     * 查看命名空间下所有表
     * @throws Exception
     */
    @Test
    public void listNamespaceTables() throws Exception{

        List<TableDescriptor> tableDescriptors = admin.listTableDescriptorsByNamespace("default".getBytes());

        for(TableDescriptor desc: tableDescriptors){
            System.out.println(desc.getTableName().getNameAsString());
        }
    }

    /**
     * 删除命名空间
     * @throws Exception
     */
    @Test
    public void dropNamespace() throws Exception{
        //1、获取命名空间下所有表
        List<TableDescriptor> tableDescriptors = admin.listTableDescriptorsByNamespace("big".getBytes());
        //2、删除命名空间下所有表
        for(TableDescriptor desc: tableDescriptors){
            //禁用表
            admin.disableTable(desc.getTableName());
            //删除表
            admin.deleteTable(desc.getTableName());
        }
        //3、删除命名空间
        admin.deleteNamespace("big");
    }

    /**
     * 创建表
     * @throws Exception
     */
    @Test
    public void createTable() throws Exception{
        //1、描述列簇
        ColumnFamilyDescriptor base_info = ColumnFamilyDescriptorBuilder.newBuilder("base_info".getBytes()).setMaxVersions(3).build();
        ColumnFamilyDescriptor extra_info = ColumnFamilyDescriptorBuilder.newBuilder("extra_info".getBytes()).build();
        //2、描述表
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf("D"))
                .setColumnFamily(base_info)
                .setColumnFamily(extra_info)
                .build();
        
        //3、创建表
        //admin.createTable(tableDescriptor);
        byte[][] splitkeys = { "11".getBytes(),"22".getBytes(),"33".getBytes() };
        admin.createTable(tableDescriptor,splitkeys);
    }

    /**
     * 修改表
     * 修改表的时候没有修改的列簇也必须带上
     * @throws Exception
     */
    @Test
    public void alterTable() throws Exception{
        //描述列簇
        ColumnFamilyDescriptor baseinfo = ColumnFamilyDescriptorBuilder.newBuilder("base_info".getBytes()).setMaxVersions(2).build();
        ColumnFamilyDescriptor extra_info = ColumnFamilyDescriptorBuilder.newBuilder("extra_info".getBytes()).build();
        //描述表
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf("bigdata:person"))
                .setColumnFamily(baseinfo)
                .setColumnFamily(extra_info)
                .build();

        admin.modifyTable(tableDescriptor);
    }

    /**
     * 插入数据
     * put '表','rowkey',''
     * @throws Exception
     */
    @Test
    public void put() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        Put put = new Put("1001".getBytes());
        put.addColumn("base_info".getBytes(),"name".getBytes(),"zhangsan".getBytes());
        put.addColumn("base_info".getBytes(),"age".getBytes(), Bytes.toBytes(20));
        put.addColumn("extra_info".getBytes(),"address".getBytes(),"shenzhen".getBytes());
        //3、插入数据
        table.put(put);
        //4、关闭表
        table.close();
    }

    /**
     * 插入多行数据
     * @throws Exception
     */
    @Test
    public void putList() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        ArrayList<Put> puts = new ArrayList<Put>();
        Put put = null;
        for(int i=0;i<=10;i++){
            put = new Put(("100"+i).getBytes());
            put.addColumn("base_info".getBytes(),"name".getBytes(),("zhangsan-"+i).getBytes());
            put.addColumn("base_info".getBytes(),"age".getBytes(), Bytes.toBytes(20+i));
            put.addColumn("extra_info".getBytes(),"address".getBytes(),"shenzhen".getBytes());

            puts.add(put);
        }


        //3、插入数据
        table.put(puts);
        //4、关闭表
        table.close();
    }

    /**
     * 根据rowkey查询数据
     * @throws Exception
     */
    @Test
    public void get() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        Get get = new Get("1001".getBytes());
        //指定查询base_info列簇的数据
        //get.addFamily("base_info".getBytes());
        get.addColumn("base_info".getBytes(),"name".getBytes());
        //3、查询
        Result result = table.get(get);
        //4、展示结果
        List<Cell> cells = result.listCells();
        for (Cell cell: cells){
            //rowkey
            String rowkey = new String(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            //列簇
            String family = new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            //列限定符
            String qualifier = new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            //值
            if(family.equals("base_info") && qualifier.equals("age")){
                int value = Bytes.toInt(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
            }else{

                String value = new String(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
            }

        }
        //5、关闭
        table.close();
    }

    /**
     * 扫描数据
     * @throws Exception
     */
    @Test
    public void scan() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        //扫描全表数据
        //Scan scan = new Scan();
        //查询某个列簇的数据
        //Scan scan = new Scan();
        //scan.addFamily("base_info".getBytes());
        //查询某个列的数据
        //Scan scan = new Scan();
        //scan.addColumn("base_info".getBytes(),"name".getBytes());
        //查询某个rowkey范围段的数据
/*        Scan scan = new Scan();
        scan.withStartRow("1001".getBytes(),false);
        scan.withStopRow("1005".getBytes(),true);*/
        //查询多个版本的数据
        Scan scan = new Scan();
        scan.readVersions(3);
        //3、查询
        ResultScanner scanner = table.getScanner(scan);
        Iterator<Result> it = scanner.iterator();
        //4、结果展示
        while (it.hasNext()){
            Result result = it.next();
            List<Cell> cells = result.listCells();
            for(Cell cell:cells){
                //rowkey
                String rowkey = new String(CellUtil.cloneRow(cell));
                //列簇
                String family = new String(CellUtil.cloneFamily(cell));
                //列限定符
                String qualifier = new String(CellUtil.cloneQualifier(cell));
                if(family.equals("base_info") && qualifier.equals("age")){
                    int value = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }else{
                    String value = new String(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }
            }
        }
        //5、关闭
        table.close();
    }

    /**
     * 删除数据
     * @throws Exception
     */
    @Test
    public void delete() throws Exception{
        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        //删除整行数据
        //Delete delete = new Delete("1002".getBytes());
        //删除cell数据
        Delete delete = new Delete("1003".getBytes());
        delete.addColumn("base_info".getBytes(),"age".getBytes());
        //3、删除
        table.delete(delete);
        //4、关闭
        table.close();
    }

    /**
     * 根据value值查询数据
     * select * from .. where name='zhangsan-1'
     * @throws Exception
     */
    @Test
    public void filterByValue() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        Scan scan = new Scan();

        //添加过滤器
        SingleColumnValueFilter filter = new SingleColumnValueFilter("base_info".getBytes(), "name".getBytes(), CompareOperator.EQUAL, "zhangsan-1".getBytes());
        scan.setFilter(filter);
        //3、查询
        ResultScanner results = table.getScanner(scan);
        Iterator<Result> it = results.iterator();
        //4、结果展示
        while (it.hasNext()){
            Result result = it.next();
            List<Cell> cells = result.listCells();
            for(Cell cell:cells){
                //rowkey
                String rowkey = new String(CellUtil.cloneRow(cell));
                //列簇
                String family = new String(CellUtil.cloneFamily(cell));
                //列限定符
                String qualifier = new String(CellUtil.cloneQualifier(cell));
                if(family.equals("base_info") && qualifier.equals("age")){
                    int value = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }else{
                    String value = new String(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }
            }
        }
        //5、关闭
        table.close();
    }

    /**
     * 模糊查询
     * select * from .. where name like '%3'
     * @throws Exception
     */
    @Test
    public void like() throws Exception{

        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        Scan scan = new Scan();

        //添加过滤器
        SingleColumnValueFilter filter = new SingleColumnValueFilter("base_info".getBytes(), "name".getBytes(),CompareOperator.EQUAL,new SubstringComparator("3"));
        scan.setFilter(filter);
        //3、查询
        ResultScanner results = table.getScanner(scan);
        Iterator<Result> it = results.iterator();
        //4、结果展示
        while (it.hasNext()){
            Result result = it.next();
            List<Cell> cells = result.listCells();
            for(Cell cell:cells){
                //rowkey
                String rowkey = new String(CellUtil.cloneRow(cell));
                //列簇
                String family = new String(CellUtil.cloneFamily(cell));
                //列限定符
                String qualifier = new String(CellUtil.cloneQualifier(cell));
                if(family.equals("base_info") && qualifier.equals("age")){
                    int value = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }else{
                    String value = new String(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }
            }
        }
        //5、关闭
        table.close();
    }

    /**
     * 复合条件查询
     * select * from .. where name = 'lisi' or (address='beijing' and age=22)
     * @throws Exception
     */
    @Test
    public void query() throws Exception{
        //1、获取Table对象
        Table table = connection.getTable(TableName.valueOf("bigdata:person"));
        //2、封装数据
        Scan scan = new Scan();

        //添加过滤器
        // address= 'beijing' and age=22
        FilterList addressAndAgeFilter = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        //address= 'beijing'
        SingleColumnValueFilter addressFilter = new SingleColumnValueFilter("base_info".getBytes(), "address".getBytes(), CompareOperator.EQUAL, "beijing".getBytes());
        //age=22
        SingleColumnValueFilter ageFilter = new SingleColumnValueFilter("base_info".getBytes(), "age".getBytes(), CompareOperator.EQUAL, Bytes.toBytes(22));

        addressAndAgeFilter.addFilter(addressFilter);
        addressAndAgeFilter.addFilter(ageFilter);

        //name='lisi'
        SingleColumnValueFilter nameFilter = new SingleColumnValueFilter("base_info".getBytes(), "name".getBytes(), CompareOperator.EQUAL, "lisi".getBytes());

        //name = 'lisi' or (address='beijing' and age=22)
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        filterList.addFilter(addressAndAgeFilter);
        filterList.addFilter(nameFilter);

        scan.setFilter(filterList);
        //3、查询
        ResultScanner results = table.getScanner(scan);
        Iterator<Result> it = results.iterator();
        //4、结果展示
        while (it.hasNext()){
            Result result = it.next();
            List<Cell> cells = result.listCells();
            for(Cell cell:cells){
                //rowkey
                String rowkey = new String(CellUtil.cloneRow(cell));
                //列簇
                String family = new String(CellUtil.cloneFamily(cell));
                //列限定符
                String qualifier = new String(CellUtil.cloneQualifier(cell));
                if(family.equals("base_info") && qualifier.equals("age")){
                    int value = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }else{
                    String value = new String(CellUtil.cloneValue(cell));
                    System.out.println("rowkey="+rowkey+",family="+family+",qualifier="+qualifier+",value="+value);
                }
            }
        }
        //5、关闭
        table.close();
    }
    /**
     * 清空表数据
     * @throws Exception
     */
    @Test
    public void truncate() throws Exception{

        //1、判断表是否存在
        if(admin.tableExists(TableName.valueOf("bigdata:person"))){
            //禁用表
            admin.disableTable(TableName.valueOf("bigdata:person"));
            //2、清空数据
            admin.truncateTable(TableName.valueOf("bigdata:person"),false);
        }
    }

    @After
    public void close() throws Exception{
        if(admin!=null) admin.close();
        if( connection!=null ) connection.close();
    }


}
