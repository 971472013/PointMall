package com.jilian.server.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 9:42 上午 2020/2/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mall implements Serializable {
    private static final long serialVersionUID = -3638067221190547619L;
    @Id
    private String id;

    private Point point;
    //( goodsID , (goodsStatus, payMethod , pointPrice ,pointType, marketPrice, supplyPrice) )
    private Map<String, Map<String, Object>> goodsList;

    private Map<String, Subject> subjectList;

    private List<Advertise> advertise;

    private List<Entrance> entrance;

    private boolean showList;

    private double interval;

    private String color;

    private List<String> subjectOrder;      //主题的id顺序

    private String title;

    private JSONObject logo;

    private double over;        //超过包邮

    private double lest;        //至少运费

    private String servePhone;

    private List<Entrance> availableEntrance;
}
