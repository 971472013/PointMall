package com.jilian.server;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.bean.Entrance;
import com.jilian.server.bean.OrderGoods;
import com.jilian.server.bean.OrderStatus;
import com.jilian.server.bean.Subject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description: 工具类
 * @Date: Create in 3:10 下午 2020/2/12
 */
public class Utils {
    public static DateTimeFormatter show = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter worldNormalX = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm:ss.SSSX");
    public static DateTimeFormatter worldNormal = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm:ss.SSS");
    public static DateTimeFormatter worldNormalZ = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");

    public static <T> Document toDocument(T object) {

        String json = JSONObject.toJSONString(object);

        Document document = Document.parse(json);

        return document;

    }

    public static Update JSONObjectToUpdate(JSONObject jsonObject) {
        Document document = Document.parse(jsonObject.toJSONString());
        document.remove("id");
        document.remove("inMall");
        return Update.fromDocument(document);
    }

    public static Update toUpdate(Object object) {
//        Update update = new Update();
//        String[] names = getFiledName(object);
//        for (String t : names) {
//            if (t.equals("serialVersionUID") || t.equals("DEFAULT_INITIAL_CAPACITY") ||
//                    t.equals("map")) {
//                continue;
//            }
//            update.set(t, getFieldValueByName(t, object));
//        }
//        return update;
        Document document = toDocument(object);
        document.remove("id");
        return Update.fromDocument(document);
    }

    public static OrderGoods JSONObject2orderGoods(JSONObject jsonObject) {
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoodsID(jsonObject.getString("goodsID"));
        orderGoods.setName(jsonObject.get("name").toString());
        orderGoods.setDescribe(jsonObject.get("describe").toString());
        orderGoods.setGoodsType(jsonObject.get("goodsType").toString());
        orderGoods.setSupplier(jsonObject.getString("supplier"));
        orderGoods.setImage(jsonObject.getJSONObject("image"));
        orderGoods.setSupplyPrice(jsonObject.getDoubleValue("supplyPrice"));
        orderGoods.setMarketPrice(jsonObject.getDoubleValue("marketPrice"));
        orderGoods.setPointPrice(jsonObject.getDoubleValue("pointPrice"));
        orderGoods.setExpress(jsonObject.getDoubleValue("express"));
        orderGoods.setPointExpress(jsonObject.getDoubleValue("pointExpress"));
        orderGoods.setPointType(jsonObject.getString("pointType"));
        orderGoods.setNum(jsonObject.getIntValue("num"));
        if (jsonObject.getString("payTime") != null) {
            try {
                orderGoods.setPayTime(LocalDateTime.parse(jsonObject.getString("payTime"), worldNormal));
            } catch (Exception e) {
                orderGoods.setPayTime(LocalDateTime.parse(jsonObject.getString("payTime"), worldNormalX));
            }
        } else {
            orderGoods.setPayTime(null);
        }
        if (jsonObject.getString("outTime") != null) {
            try {
                orderGoods.setOutTime(LocalDateTime.parse(jsonObject.getString("outTime"), worldNormal));
            } catch (Exception e) {
                orderGoods.setOutTime(LocalDateTime.parse(jsonObject.getString("outTime"), worldNormalX));
            }
        } else {
            orderGoods.setOutTime(null);
        }
        if (jsonObject.getString("receiveTime") != null) {
            try {
                orderGoods.setReceiveTime(LocalDateTime.parse(jsonObject.getString("receiveTime"), worldNormal));
            } catch (Exception e) {
                orderGoods.setReceiveTime(LocalDateTime.parse(jsonObject.getString("receiveTime"), worldNormalX));
            }
        } else {
            orderGoods.setReceiveTime(null);
        }
        if (jsonObject.getString("doneTime") != null) {
            try {
                orderGoods.setDoneTime(LocalDateTime.parse(jsonObject.getString("doneTime"), worldNormal));
            } catch (Exception e) {
                orderGoods.setDoneTime(LocalDateTime.parse(jsonObject.getString("doneTime"), worldNormalX));
            }
        } else {
            orderGoods.setDoneTime(null);
        }

        orderGoods.setSource(jsonObject.getString("source"));
        orderGoods.setNeedDistribution(jsonObject.getString("needDistribution"));
        orderGoods.setExpressCompany(jsonObject.getString("expressCompany"));
        orderGoods.setExpressID(jsonObject.getString("expressID"));
        orderGoods.setBackExpressCompany(jsonObject.getString("backExpressCompany"));
        orderGoods.setBackExpressID(jsonObject.getString("backExpressID"));
        orderGoods.setOrderStatus(OrderStatus.valueOf(jsonObject.getString("orderStatus")));
        return orderGoods;
    }

    public static Subject JSONObject2subject(JSONObject jsonObject) {
        Subject subject;
        try {
            subject = new Subject(jsonObject.getString("id"), jsonObject.getString("name"),
                    jsonObject.getJSONObject("image"), jsonObject.getString("type"),
                    jsonObject.getString("status"), jsonObject.getString("describe"),
                    LocalDateTime.parse(jsonObject.getString("createTime"), worldNormal),
                    Arrays.asList(jsonObject.getJSONArray("goodsList").toArray(new String[0])));
        } catch (Exception e) {
            subject = new Subject(jsonObject.getString("id"), jsonObject.getString("name"),
                    jsonObject.getJSONObject("image"), jsonObject.getString("type"),
                    jsonObject.getString("status"), jsonObject.getString("describe"),
                    LocalDateTime.parse(jsonObject.getString("createTime"), worldNormalX),
                    Arrays.asList(jsonObject.getJSONArray("goodsList").toArray(new String[0])));
        }
        return subject;
    }

    public static JSONObject toJSONObject(Object object) {
        JSONObject jsonObject = new JSONObject();
        String[] names = getFiledName(object);
        for (String t : names) {
            if (t.equals("serialVersionUID") || t.equals("DEFAULT_INITIAL_CAPACITY") ||
                    t.equals("map")) {
                continue;
            }
            jsonObject.put(t, getFieldValueByName(t, object));
        }
        return jsonObject;
    }

    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
//            System.out.println(fields[i].getName());
        }
        return fieldNames;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 系统内快捷入口列表
     *
     * @return
     */
    public static List<Entrance> getEntrance() {
        List<Entrance> entrance = new ArrayList<>();
        JSONObject e1 = new JSONObject();
        e1.put("name", "entranceImage/居家厨具.png");
        e1.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E5%B1%85%E5%AE%B6%E5%8E%A8%E5%85%B7.png");
        JSONObject e2 = new JSONObject();
        e2.put("name", "entranceImage/家电数码.png");
        e2.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E5%AE%B6%E7%94%B5%E6%95%B0%E7%A0%81.png");
        JSONObject e3 = new JSONObject();
        e3.put("name", "entranceImage/粮油生鲜.png");
        e3.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E7%B2%AE%E6%B2%B9%E7%94%9F%E9%B2%9C.png");
        JSONObject e4 = new JSONObject();
        e4.put("name", "entranceImage/美食酒水.png");
        e4.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E7%BE%8E%E9%A3%9F%E9%85%92%E6%B0%B4.png");
        JSONObject e5 = new JSONObject();
        e5.put("name", "entranceImage/个护清洁.png");
        e5.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E4%B8%AA%E6%8A%A4%E6%B8%85%E6%B4%81.png");
        JSONObject e6 = new JSONObject();
        e6.put("name", "entranceImage/母婴玩具.png");
        e6.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E6%AF%8D%E5%A9%B4%E7%8E%A9%E5%85%B7.png");
        JSONObject e7 = new JSONObject();
        e7.put("name", "entranceImage/户外车品.png");
        e7.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/%E6%88%B7%E5%A4%96%E8%BD%A6%E5%93%81.png");
        JSONObject e8 = new JSONObject();
        e8.put("name", "entranceImage/医疗计生.png");
        e8.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/entranceImage/医疗计生.png");
        Entrance en1 = new Entrance(new ObjectId().toString(), "居家厨具", e1, "厨具");
        Entrance en2 = new Entrance(new ObjectId().toString(), "家电数码", e2, "数码");
        Entrance en3 = new Entrance(new ObjectId().toString(), "粮油生鲜", e3, "食品");
        Entrance en4 = new Entrance(new ObjectId().toString(), "美食酒水", e4, "酒类");
        Entrance en5 = new Entrance(new ObjectId().toString(), "个护清洁", e5, "美妆");
        Entrance en6 = new Entrance(new ObjectId().toString(), "母婴玩具", e6, "母婴");
        Entrance en7 = new Entrance(new ObjectId().toString(), "户外车品", e7, "户外");
        Entrance en8 = new Entrance(new ObjectId().toString(), "医疗计生", e8, "医药");
        entrance.add(en1);
        entrance.add(en2);
        entrance.add(en3);
        entrance.add(en4);
        entrance.add(en5);
        entrance.add(en6);
        entrance.add(en7);
        entrance.add(en8);
        return entrance;
    }

    public static String convertStreamToString(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = file.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
