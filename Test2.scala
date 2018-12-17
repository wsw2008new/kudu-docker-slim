package com.qez.bigdata

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.kudu.{ColumnSchema, Schema, Type}
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.bson.Document

import collection.JavaConverters._
 
object Test2 {

  def main (args: Array[String]) { 

    val conf = new SparkConf()
      .setAppName("mongozips")
      .set("spark.driver.host", "localhost")
      .set("spark.testing.memory", "2147480000")
      .setMaster("local[*]")

    val sps = SparkSession.builder.config(conf).getOrCreate();
    val kuduContext = new KuduContext("localhost:7051", sps.sparkContext)

    //val df = MongoSpark.load(sps.sparkContext,new ReadConfig("test911","shops",Option.apply("mongodb://192.168.0.133/"))).toDF().select("shopId","shopName")//,"assignBranches"
    val df = MongoSpark.load(sps.sparkContext,new ReadConfig("test911","syncsaleflows",Option.apply("mongodb://192.168.0.133/")))
      .withPipeline(Seq(Document.parse("{$match:{ }}")
       ,Document.parse("{$project:{_id:0,com_no:1,shopId:1,branch_no:1,card_no:1,card_id:1,sale_money:1,sale_man:1,flow_no:1,flow_id:1,oper_date:1}}")
      )).toDF()
      .select("shopId","branch_no","card_no","card_id","sale_money","sale_man","flow_no","flow_id","oper_date")



//    val df = MongoSpark.load(sps.sparkContext,new ReadConfig("test911","shops",Option.apply("mongodb://192.168.0.111/")))
//      .withPipeline(Seq(Document.parse("{$match:{shopId:'000003'}}")
//        ,Document.parse("{$project:{_id:0,shopId:1,shopName:1}}")
//      )).toDF()
//    val columnList = List(
//    new ColumnSchemaBuilder("shopId", Type.STRING).key(true).build(),
//    new ColumnSchemaBuilder("shopName", Type.STRING).key(false).build() )
//    val schema = new Schema(columnList.asJava)
//
//    val e = kuduContext.tableExists("shops");
//
//    if(!e){
//      kuduContext.createTable("shops",
//        schema,
//        new CreateTableOptions().setNumReplicas(1).addHashPartitions(List("shopId").asJava,2))
//    }
//    df.show()

        val columnList = List(
          new ColumnSchemaBuilder("com_no", Type.INT32).key(true).build(),
          new ColumnSchemaBuilder("shopId", Type.STRING).key(true).nullable(true).build(),
          new ColumnSchemaBuilder("branch_no", Type.STRING).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("card_no", Type.STRING).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("card_id", Type.STRING).key(false).build(),
          new ColumnSchemaBuilder("sale_money", Type.DOUBLE).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("sale_man", Type.STRING).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("flow_no", Type.STRING).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("flow_id", Type.INT32).key(false).nullable(true).build(),
          new ColumnSchemaBuilder("oper_date", Type.STRING).key(false).nullable(true).build()

        )
        val schema = new Schema(columnList.asJava)

     val e = kuduContext.tableExists("flows")
     if(e){
       kuduContext.deleteTable("flows");
     }

      kuduContext.createTable("flows",
        schema,
        new CreateTableOptions().setNumReplicas(1).addHashPartitions(List("shopID","com_no").asJava,2))





    kuduContext.upsertRows(df, "flows")

    val dataDF = sps.read.options(Map("kudu.master" -> "127.0.0.1:7051","kudu.table" -> "flows")).kudu
    dataDF.registerTempTable("flows")
    sps.sql("select branch_no,sum(sale_money) from flows group by branch_no").show()
    //sps.sql("select shopId,card_id,sum(sale_money) from syncsaleflows group by shopId,card_id").show()
  }


}
