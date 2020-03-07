package com.jilian.server.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.jilian.server.AliyunService;
import com.jilian.server.IdWorker;
import com.jilian.server.bean.*;
import com.jilian.server.jwt.JwtUtil;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description: 商城端登录
 * @Date: Create in 1:49 下午 2020/2/5
 */
@Service
public class MallLoginService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MallCustomerService mallCustomerService;
    @Autowired
    private AliyunService aliyunService;

    /**
     * 发送验证码
     *
     * @param jsonObject
     * @return
     */
    public boolean sendSMS(JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String mallID = jsonObject.getString("mallID");
        String code = creatSms();
        boolean mark = false;
        try {
            mark = aliyunService.sendAliyun(phone, code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (mark) {
            updateDB(phone, code, mallID);
        }
        return true;
    }

    /**
     * 随机生成验证码
     * @return
     */
    private String creatSms(){
        return  ""+((int)(Math.random()*99999)+100);
    }

    /**
     * 在数据库中更新验证码信息
     *
     * @param phone
     * @param code
     * @param mallID
     */
    private void updateDB(String phone, String code, String mallID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(phone).and("belongMall").is(mallID));
        Customer find = mongoTemplate.findOne(query, Customer.class, "mall_customer");
        if (find == null) {
            String customerID = new ObjectId().toString();

            Customer customer = new Customer(customerID, phone, phone, code, LocalDateTime.now().plusMinutes(5),
                    "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, mallID, LocalDateTime.now());
            mongoTemplate.insert(customer, "mall_customer");
        } else {
            Update update = new Update();
            update.set("sms", code);
            update.set("smsDDL", LocalDateTime.now().plusMinutes(5));
            update.set("password", 123);
            mongoTemplate.upsert(query, update, "mall_customer");
        }
    }

    public Result loginWithPassword(String mallID, String phone, String password) {
        JSONObject object = new JSONObject();
        Customer customer = mallCustomerService.findCustomerByPhoneAndMall(mallID, phone);
        if(customer == null){
            return ResultFactory.buildFailResult("登录失败,用户不存在");
        } else {
            if(!customer.getPassword().equals(password)){
                return ResultFactory.buildFailResult("登录失败,密码错误");
            } else {
                String token = JwtUtil.createCustomerJWT(mallID, phone, password);
                Query query = new Query();
                query.addCriteria(Criteria.where("mallID").is(mallID).and("phone").is(phone));
                Update update = new Update();
                update.set("lastLogin", LocalDateTime.now());
                mongoTemplate.upsert(query, update, "mall_customer");
                object.put("token", token);
                return ResultFactory.buildSuccessResult(object);
            }
        }
    }

    /**
     * 使用验证码登录
     * @param mallID
     * @param phone
     * @param sms
     * @return
     */
    public Result loginWithSMS(String mallID, String phone, String sms) {
        JSONObject object = new JSONObject();
        Customer customer = mallCustomerService.findCustomerByPhoneAndMall(mallID, phone);
        if(customer == null){
            return ResultFactory.buildFailResult("登录失败,用户不存在");
        } else {
            LocalDateTime ddl = customer.getSmsDDL();
            if(ddl.isBefore(LocalDateTime.now())) {
                return ResultFactory.buildFailResult("登录失败,验证码已超时");
            } else {
                if(customer.getSms().equals(sms)){
                    String token = JwtUtil.createCustomerJWT(mallID, phone, sms);
                    object.put("token", token);
                    Query query = new Query();
                    query.addCriteria(Criteria.where("belongMall").is(mallID).and("phone").is(phone));
                    Update update = new Update();
                    update.set("lastLogin", LocalDateTime.now());
                    mongoTemplate.upsert(query, update, "mall_customer");
                    return ResultFactory.buildSuccessResult(object);
                } else {
                    return ResultFactory.buildFailResult("登录失败,验证码错误");
                }
            }
        }
    }

    /**
     *
     *生成测试数据
     */
    public void generateData() {
        mongoTemplate.dropCollection("mall_customer");
        mongoTemplate.dropCollection("order");
        mongoTemplate.dropCollection("goods");
        mongoTemplate.dropCollection("admin_user");
        mongoTemplate.dropCollection("mall");
        mongoTemplate.dropCollection("point");
        mongoTemplate.dropCollection("point_record");
        adminData();
        mallData();
    }

    String mallID = new ObjectId().toString(), userID = new ObjectId().toString(),
            customerID = new ObjectId().toString(),
            g1 = IdWorker.getGoodsID(), g2 = IdWorker.getGoodsID(), g3 = IdWorker.getGoodsID(),
            sb1 = new ObjectId().toString(), sb2 = new ObjectId().toString(),
            ad1 = new ObjectId().toString(), ad2 = new ObjectId().toString();

    /**
     * admin端测试数据
     */
    private void adminData() {
        List<String> sg1 = new ArrayList<>();
        sg1.add(g1);
        sg1.add(g2);
        sg1.add(g3);
        JSONObject sbi1 = new JSONObject();
        JSONObject sbi2 = new JSONObject();
        sbi1.put("name", "subjectImage/专题1.jpg");
        sbi2.put("name", "subjectImage/专题2.jpg");
        sbi1.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/subjectImage/%E4%B8%93%E9%A2%981.jpg");
        sbi2.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/subjectImage/%E4%B8%93%E9%A2%982.jpg");
        Subject subject1 =
                new Subject(
                        sb1,
                        "年货",
                        sbi1,
                        "商品专题",
                        "上架",
                        "囤年货",
                        LocalDateTime.now(),
                        sg1);
        Subject subject2 = new Subject(sb2, "停运", sbi2,
                "商品专题", "上架", "物流停运通知", LocalDateTime.now(), new ArrayList<>());
        Map<String, Subject> subjectMap = new HashMap<>();
        subjectMap.put(sb1, subject1);
        subjectMap.put(sb2, subject2);

        JSONObject pointLogo = new JSONObject();
        pointLogo.put("name", "vant/cat.jpeg");
        pointLogo.put("url", "https://img.yzcdn.cn/vant/cat.jpeg");
        Point point = new Point("E豆", 1, true, pointLogo,
                "无", "无", "贵州工商银行", sbi1);

        Map<String, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> good1 = new HashMap<>();
        good1.put("goodsStatus", "上架");
        good1.put("payMethod", PayMethod.ONLY_POINT);
        good1.put("pointPrice", 2);
        good1.put("marketPrice", 2);
        good1.put("supplyPrice", 2);
        good1.put("pointType", point.getName());
        Map<String, Object> good2 = new HashMap<>();
        good2.put("goodsStatus", "上架");
        good2.put("payMethod", PayMethod.ONLY_POINT);
        good2.put("pointPrice", 6.9);
        good2.put("marketPrice", 6.9);
        good2.put("supplyPrice", 5);
        good2.put("pointType", point.getName());
        Map<String, Object> good3 = new HashMap<>();
        good3.put("goodsStatus", "上架");
        good3.put("payMethod", PayMethod.ONLY_POINT);
        good3.put("pointPrice", 3.2);
        good3.put("marketPrice", 3.2);
        good3.put("supplyPrice", 5);
        good3.put("pointType", point.getName());
        map.put(g1, good1);
        map.put(g2, good2);
        map.put(g3, good3);

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

        List<Advertise> advertiseList = new ArrayList<>();
        JSONObject advi1 = new JSONObject();
        advi1.put("name", "advertiseImage/广告1.png");
        advi1.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/advertiseImage/%E5%B9%BF%E5%91%8A1.png");
        JSONObject advi2 = new JSONObject();
        advi2.put("name", "advertiseImage/广告2.jpg");
        advi2.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/advertiseImage/%E5%B9%BF%E5%91%8A2.jpg");
        Advertise adv1 = new Advertise(new ObjectId().toString(), "广告1", advi1, "");
        Advertise adv2 = new Advertise(new ObjectId().toString(), "广告2", advi2, "");
        advertiseList.add(adv1);
        advertiseList.add(adv2);
        List<String> subjectOrder = new ArrayList<>();
        subjectOrder.add(sb1);
        subjectOrder.add(sb2);
        Mall mall = new Mall(mallID, point, map, subjectMap, advertiseList, entrance, true, 3, "#000000",
                subjectOrder, "E豆商城", pointLogo, 50, 8, "", entrance);
        mongoTemplate.insert(mall, "mall");

        List<JSONObject> mallList = new ArrayList<>();
        JSONObject mallLiOb = new JSONObject();
        mallLiOb.put("id", mallID);
        mallLiOb.put("title", "E豆商城");
        mallList.add(mallLiOb);
        UserInfo info = new UserInfo();
        info.setRoles(new ArrayList<String>() {
            private static final long serialVersionUID = 7870829345696302569L;

            {
                add("admin");
            }
        });
        info.setName("Super Admin");
        info.setIntroduction("I am a super administrator");
        info.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        userID = new ObjectId().toString();
        User user = new User();
        user.setId(userID);
        user.setUsername("admin");
        user.setPassword("admin");
        user.setToken("admin-token");
        user.setUserInfo(info);
        user.setMallList(mallList);
        mongoTemplate.insert(user, "admin_user");


    }

    /**
     * mall端测试数据
     */
    private void mallData() {
        Address address1 = new Address(ad1, "张三", "13000000000", "北京市", "北京市",
                "东城区", "文三路 138 号东方通信大厦 7 楼 501 室",
                "北京市东城区文三路 138 号东方通信大厦 7 楼 501 室", "110101",
                false, "663700");
        Address address2 = new Address(ad2, "李四", "13000000022", "河北省", "石家庄市",
                "长安区", "文三路 138 号东方通信大厦 7 楼 501 室",
                "河北省石家庄市长安区66路77号", "130102",
                false, "663722");
        List<Address> addressList = new ArrayList<>();
        addressList.add(address1);
        addressList.add(address2);

        List<JSONObject> imgList1 = new ArrayList<>();
        JSONObject o1 = new JSONObject();
        o1.put("name", "goodsImage/梨.jpeg");
        o1.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/goodsImage/%E6%A2%A8.jpeg");
        JSONObject o2 = new JSONObject();
        o2.put("name", "goodsImage/香蕉.jpeg");
        o2.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/goodsImage/%E9%A6%99%E8%95%89.jpeg");
        JSONObject o3 = new JSONObject();
        o3.put("name", "goodsImage/苹果.jpeg");
        o3.put("url", "https://xmall123.oss-cn-shenzhen.aliyuncs.com/goodsImage/%E8%8B%B9%E6%9E%9C.jpeg");
        imgList1.add(o2);
        Goods goods1 = new Goods(g1, "进口香蕉", "约250g，2根", "水果",
                2, 2, "京东", 666,
                "123", imgList1, userID, "是");
        List<JSONObject> imgList2 = new ArrayList<>();
        imgList2.add(o1);

        Goods goods2 = new Goods(g2, "陕西蜜梨", "约600g", "水果",
                6.9, 5, "京东", 777,
                "123", imgList2, userID, "是");

        List<JSONObject> imgList3 = new ArrayList<>();
        imgList3.add(o3);

        Goods goods3 = new Goods(g3, "美国伽力果", "约680g/3个", "水果",
                3.2, 5, "京东", 177,
                "123", imgList3, userID, "是");

        List<Pair<String, Integer>> goodsList = new ArrayList<>();
        goodsList.add(new Pair<>(goods3.getId(), 3));
        goodsList.add(new Pair<>(goods2.getId(), 1));
        goodsList.add(new Pair<>(goods1.getId(), 2));

        List<Goods> documents = new ArrayList<>();
        documents.add(goods1);
        documents.add(goods2);
        documents.add(goods3);


        Customer customer = new Customer(customerID, "111", "222", "111",
                LocalDateTime.now().plusDays(30), "", addressList,
                goodsList, null, 99999, mallID, LocalDateTime.now());

        mongoTemplate.insert(customer, "mall_customer");
        mongoTemplate.insert(documents, "goods");

        OrderGoods orderGoods1 = new OrderGoods(g3, "美国伽力果", "约680g/3个", "水果", "京东",
                imgList3.get(0), 5, 3.2, 3.2, "E豆", 4,
                LocalDateTime.now(), null, null, null, "京东", "是",
                "顺丰", "321", OrderStatus.待收货, "", "", 0, 0);
        OrderGoods orderGoods2 = new OrderGoods(g2, "陕西蜜梨", "约600g", "水果", "京东",
                imgList2.get(0), 5, 6.9, 6.9, "E豆", 5,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "京东", "是",
                "京东", "123", OrderStatus.已完成, "", "", 0, 0);
        OrderGoods orderGoods3 = new OrderGoods(g1, "进口香蕉", "约250g，2根", "水果", "京东",
                imgList1.get(0), 2, 2, 2, "E豆", 3,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, "京东", "是",
                "", "", OrderStatus.待发货, "", "", 0, 0);
        Map<String, OrderGoods> orderGoodsList1 = new HashMap<>();
        Map<String, OrderGoods> orderGoodsList2 = new HashMap<>();
        orderGoodsList1.put(g3, orderGoods1);
        orderGoodsList1.put(g2, orderGoods2);
        orderGoodsList1.put(g1, orderGoods3);
        orderGoodsList2.put(g3, orderGoods1);
        Order order1 = new Order(IdWorker.getOrderID(), "111", customerID, userID, mallID,
                LocalDateTime.now(), PayMethod.ONLY_POINT,
                orderGoodsList1, 53.3, 53.3, 12, "E豆", address1, null, 0, 0);
        Order order2 = new Order(IdWorker.getOrderID(), "111", customerID, userID, mallID,
                LocalDateTime.now(), PayMethod.ONLY_POINT,
                orderGoodsList2, 12.8, 12.8, 4, "E豆", address2, null, 0, 0);
        List<Order> orderList = new ArrayList<>();
        orderList.add(order1);
        orderList.add(order2);
        mongoTemplate.insert(orderList, "order");
    }
}
