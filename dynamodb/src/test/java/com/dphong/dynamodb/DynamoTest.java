package com.dphong.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DynamoTest {

    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private DynamoDB dynamoDB = new DynamoDB(client);

    private String testTableName = "testTableName";

    private Table testTable;

    @BeforeEach
    private void setTableName() {
        this.testTable = dynamoDB.getTable(testTableName);
    }

    @Test
    public void selectTest() {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("PrimaryKey = :val1")
                .withValueMap(new ValueMap()
                        .withInt(":val1", 1)
                );

        ItemCollection<QueryOutcome> items = testTable.query(spec);
        items.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void selectIndexTest() {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("PrimaryKey = :val1")
                .withValueMap(new ValueMap()
                        .withInt(":val1", 2)
                );


        ItemCollection<QueryOutcome> items = testTable.query(spec);
        items.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void selectByScanTest() {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":val1", new AttributeValue().withS("dphong"));

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("indexOrField = :val1")
                .withValueMap(new ValueMap()
                        .withString(":val1", "dphong")
                );

        ItemCollection<ScanOutcome> items = testTable.scan(scanSpec);
        items.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void insertTest() {
        Item item = new Item()
                .withPrimaryKey("PrimaryKey", 4)
                .withString("field1", "dphong")
                .withString("field2", getNowDate())
                .withString("field3", "테스트 필드~~~")
                .withString("field4", "테스트 필드!!!")
                ;

        testTable.putItem(item);
    }

    @Test
    public void deleteTest() {
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("PrimaryKey", 4)
                    .withConditionExpression("field1 = :val1")
                    .withValueMap(new ValueMap()
                            .withString(":val1", "dphong")
                    );

            DeleteItemOutcome outcome = testTable.deleteItem(deleteItemSpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long dynamoTtl(){
        Calendar cal = Calendar.getInstance(); //current date and time
        cal.add(Calendar.DAY_OF_MONTH, 6); //add days
        long ttl =  (cal.getTimeInMillis() / 1000L);
        return ttl;
    }

    private String getNowDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
