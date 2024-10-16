/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.sdk.transform.process;

import org.apache.inlong.sdk.transform.decode.SourceDecoderFactory;
import org.apache.inlong.sdk.transform.encode.SinkEncoderFactory;
import org.apache.inlong.sdk.transform.pojo.CsvSourceInfo;
import org.apache.inlong.sdk.transform.pojo.FieldInfo;
import org.apache.inlong.sdk.transform.pojo.KvSinkInfo;
import org.apache.inlong.sdk.transform.pojo.TransformConfig;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * TestArithmeticFunctionsTransformProcessor
 * description: test the arithmetic functions in transform processor
 */
public class TestTransformArithmeticFunctionsProcessor {

    private static final List<FieldInfo> srcFields = new ArrayList<>();
    private static final List<FieldInfo> dstFields = new ArrayList<>();
    private static final CsvSourceInfo csvSource;
    private static final KvSinkInfo kvSink;

    static {
        for (int i = 1; i < 5; i++) {
            FieldInfo field = new FieldInfo();
            field.setName("numeric" + i);
            srcFields.add(field);
        }
        FieldInfo field = new FieldInfo();
        field.setName("result");
        dstFields.add(field);
        csvSource = new CsvSourceInfo("UTF-8", '|', '\\', srcFields);
        kvSink = new KvSinkInfo("UTF-8", dstFields);
    }

    @Test
    public void testModuloFunction() throws Exception {
        String transformFunctionSql = "select mod(numeric1,100) from source";
        String transformExpressionSql = "select numeric1 % 100 from source";
        List<String> output1, output2;
        String data;
        TransformConfig functionConfig = new TransformConfig(transformFunctionSql);
        TransformProcessor<String, String> functionProcessor = TransformProcessor
                .create(functionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        TransformConfig expressionConfig = new TransformConfig(transformExpressionSql);
        TransformProcessor<String, String> expressionProcessor = TransformProcessor
                .create(expressionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));

        // case1: "mod(3.1415926,100)" and "3.1415926 % 100"
        data = "3.1415926|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=3.1415926", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=3.1415926", output2.get(0));

        // case2: "mod(-3.1415926,100)" and "-3.1415926 % 100"
        data = "-3.1415926|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-3.1415926", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-3.1415926", output2.get(0));

        // case3: "mod(320,100)" and "320 % 100"
        data = "320|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=20", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=20", output2.get(0));

        // case4: "mod(-320,100)" and "-320 % 100"
        data = "-320|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-20", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-20", output2.get(0));

        transformFunctionSql = "select mod(numeric1,-10) from source";
        transformExpressionSql = "select numeric1 % -10 from source";
        functionConfig = new TransformConfig(transformFunctionSql);
        functionProcessor = TransformProcessor
                .create(functionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        expressionConfig = new TransformConfig(transformExpressionSql);
        expressionProcessor = TransformProcessor
                .create(expressionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));

        // case5: "mod(9,-10)" and "9 % -10"
        data = "9|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=9", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=9", output2.get(0));

        // case6: "mod(-13,-10)" and "-13 % -10"
        data = "-13|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-3", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-3", output2.get(0));

        // case7: "mod(-13.14,-10)" and "-13.14 % -10"
        data = "-13.14|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-3.14", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-3.14", output2.get(0));

        // case8: "mod(13.14,-10)" and "13.14 % -10"
        data = "13.14|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=3.14", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=3.14", output2.get(0));

        transformFunctionSql = "select mod(numeric1,-3.14) from source";
        transformExpressionSql = "select numeric1 % -3.14 from source";
        functionConfig = new TransformConfig(transformFunctionSql);
        functionProcessor = TransformProcessor
                .create(functionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        expressionConfig = new TransformConfig(transformExpressionSql);
        expressionProcessor = TransformProcessor
                .create(expressionConfig, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));

        // case9: "mod(9,-3.14)" and "9 % -3.14"
        data = "9|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=2.72", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=2.72", output2.get(0));

        // case10: "mod(-9,-3.14)" and "-9 % -3.14"
        data = "-9|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-2.72", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-2.72", output2.get(0));

        // case11: "mod(-13.14,-3.14)" and "-13.14 % -3.14"
        data = "-13.14|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=-0.58", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=-0.58", output2.get(0));

        // case12: "mod(13.14,-3.14)" and "13.14 % -3.14"
        data = "13.14|4a|4|8";
        output1 = functionProcessor.transform(data);
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals("result=0.58", output1.get(0));
        output2 = expressionProcessor.transform(data);
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals("result=0.58", output2.get(0));

    }

    @Test
    public void testRoundFunction() throws Exception {
        String transformSql = "select round(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: round(3.14159265358979323846)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("3.14159265358979323846|4|6|8");
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=3");
        // case2: round(3.5)
        List<String> output2 = processor.transform("3.5|4|6|8");
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=4");

        transformSql = "select round(numeric1,numeric2) from source";
        config = new TransformConfig(transformSql);
        // case3: round(3.14159265358979323846,10)
        processor = TransformProcessor.create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output3 = processor.transform("3.14159265358979323846|10|6|8");
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=3.1415926536");
        // case4: round(13.14159265358979323846,-1)
        List<String> output4 = processor.transform("13.14159265358979323846|-1|6|8");
        Assert.assertEquals(1, output4.size());
        Assert.assertEquals(output4.get(0), "result=10.0");
    }

    @Test
    public void testPowerFunction() throws Exception {
        String transformSql = "select power(numeric1, numeric2) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: 2^4
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=16.0");
        // case2: 2^(-2)
        List<String> output2 = processor.transform("2|-2|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=0.25");
        // case3: 4^(0.5)
        List<String> output3 = processor.transform("4|0.5|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=2.0");
    }

    @Test
    public void testAbsFunction() throws Exception {
        String transformSql = "select abs(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: |2|
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=2");
        // case2: |-4.25|
        List<String> output2 = processor.transform("-4.25|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=4.25");
    }

    @Test
    public void testSqrtFunction() throws Exception {
        String transformSql = "select sqrt(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: sqrt(9)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("9|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=3.0");
        // case2: sqrt(5)
        List<String> output2 = processor.transform("5|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=2.23606797749979");
    }

    @Test
    public void testLnFunction() throws Exception {
        String transformSql = "select ln(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: ln(1)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case2: ln(10)
        List<String> output2 = processor.transform("10|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=2.302585092994046");
    }

    @Test
    public void testLog10Function() throws Exception {
        String transformSql = "select log10(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: log10(1)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case2: log10(1000)
        List<String> output2 = processor.transform("1000|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=3.0");
    }

    @Test
    public void testLog2Function() throws Exception {
        String transformSql = "select log2(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: log2(1)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case2: log2(32)
        List<String> output2 = processor.transform("32|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=5.0");
    }

    @Test
    public void testLogFunction() throws Exception {
        String transformSql1 = "select log(numeric1) from source";
        TransformConfig config1 = new TransformConfig(transformSql1);
        // case1: ln(1)
        TransformProcessor<String, String> processor1 = TransformProcessor
                .create(config1, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor1.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        String transformSql2 = "select log(numeric1, numeric2) from source";
        TransformConfig config2 = new TransformConfig(transformSql2);
        // case2: log2(8)
        TransformProcessor<String, String> processor2 = TransformProcessor
                .create(config2, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output2 = processor2.transform("2|8|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=3.0");
        // case3: log10(100)
        TransformProcessor<String, String> processor3 = TransformProcessor
                .create(config2, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output3 = processor3.transform("10|100|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=2.0");
    }

    @Test
    public void testExpFunction() throws Exception {
        String transformSql = "select exp(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: e^0
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=1.0");
        // case2: e^2
        List<String> output2 = processor.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=7.38905609893065");
    }

    @Test
    public void testCeilFunction() throws Exception {
        String transformSql = "select ceil(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: ceil(1.23)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("1.23|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=2.0");
        // case2: ceil(3)
        List<String> output2 = processor.transform("3|-2|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=3.0");
        // case3: ceil(-5.67)
        List<String> output3 = processor.transform("-5.67|0.5|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=-5.0");
    }

    @Test
    public void testFloorFunction() throws Exception {
        String transformSql = "select floor(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        // case1: floor(1.23)
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        List<String> output1 = processor.transform("1.23|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=1.0");
        // case2: floor(3)
        List<String> output2 = processor.transform("3|-2|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=3.0");
        // case3: floor(-5.67)
        List<String> output3 = processor.transform("-5.67|0.5|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=-6.0");
    }

    @Test
    public void testSinFunction() throws Exception {
        String transformSql = "select sin(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: sin(0)
        List<String> output1 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
    }

    @Test
    public void testSinhFunction() throws Exception {
        String transformSql = "select sinh(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case1: sinh(0)
        List<String> output1 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case2: sinh(1)
        List<String> output2 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=1.1752011936438014");
        // case3: sinh(2)
        List<String> output3 = processor.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=3.626860407847019");
    }

    @Test
    public void testCosFunction() throws Exception {
        String transformSql = "select cos(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: cos(0)
        List<String> output1 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=1.0");
    }

    @Test
    public void testAcosFunction() throws Exception {
        String transformSql = "select acos(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case1: acos(1)
        List<String> output1 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case2: acos(0)
        List<String> output2 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=1.5707963267948966");
        // case3: acos(-1)
        List<String> output3 = processor.transform("-1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=3.141592653589793");
    }

    @Test
    public void testTanFunction() throws Exception {
        String transformSql = "select tan(numeric1) from source";
        TransformConfig config = new TransformConfig(transformSql);
        TransformProcessor<String, String> processor = TransformProcessor
                .create(config, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: tan(0)
        List<String> output1 = processor.transform("0|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=0.0");
        // case: tan(1)
        List<String> output2 = processor.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=1.5574077246549023");
        // case: tan(2)
        List<String> output3 = processor.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        Assert.assertEquals(output3.get(0), "result=-2.185039863261519");
    }

    @Test
    public void testBinFunction() throws Exception {
        String transformSql1 = "select bin(numeric1) from source";
        TransformConfig config1 = new TransformConfig(transformSql1);
        TransformProcessor<String, String> processor1 = TransformProcessor
                .create(config1, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: bin(4)
        List<String> output1 = processor1.transform("4|5|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=100");
        String transformSql2 = "select bin() from source";
        TransformConfig config2 = new TransformConfig(transformSql2);
        TransformProcessor<String, String> processor2 = TransformProcessor
                .create(config2, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: bin()
        List<String> output2 = processor2.transform("1|2|3|4", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output2.get(0), "result=null");
    }

    @Test
    public void testRandFunction() throws Exception {
        String transformSql1 = "select rand(numeric1) from source";
        TransformConfig config1 = new TransformConfig(transformSql1);
        TransformProcessor<String, String> processor1 = TransformProcessor
                .create(config1, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: rand(1)
        List<String> output1 = processor1.transform("1|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output1.size());
        Assert.assertEquals(output1.get(0), "result=" + new Random(1).nextDouble());
        // case: rand(2)
        List<String> output2 = processor1.transform("2|4|6|8", new HashMap<>());
        Assert.assertEquals(1, output2.size());
        Assert.assertEquals(output2.get(0), "result=" + new Random(2).nextDouble());
        String transformSql2 = "select rand() from source";
        TransformConfig config2 = new TransformConfig(transformSql2);
        TransformProcessor<String, String> processor2 = TransformProcessor
                .create(config2, SourceDecoderFactory.createCsvDecoder(csvSource),
                        SinkEncoderFactory.createKvEncoder(kvSink));
        // case: rand()
        List<String> output3 = processor2.transform("|||", new HashMap<>());
        Assert.assertEquals(1, output3.size());
        double result = Double.parseDouble(output3.get(0).substring(7));
        Assert.assertTrue(result >= 0.0 && result < 1.0);
    }

}
