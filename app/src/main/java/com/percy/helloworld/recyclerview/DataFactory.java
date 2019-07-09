package com.percy.helloworld.recyclerview;

import java.util.ArrayList;
import java.util.List;

public class DataFactory {
    //更新于18：40：51
    public static final String[] No= {"1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10.","11.", "12.", "13.", "14.", "15.", "16.", "17.", "18.", "19.", "20.","21.", "22.", "23.", "24.", "25.", "26.", "27.", "28.", "29.", "30."};
    public static final String[] TITLE= {"表面光鲜亮丽其实穿着大裤衩", "朱广权被手语翻译老师吐槽", "古天乐丧尸式表演", "网友变身吴亦凡", "来自亲爸的嫌弃", "长沙橘子洲被淹", "沉睡魔咒2预告", "当武校女孩遇到抢劫", "神似迪丽热巴的小姐姐", "蕾哈娜中国风封面","心疼瓢哥", "郭艾伦晃倒对手", "中国仪仗队女兵惊艳现场", "在床上玩手机最舒服的时候", "朱广权批夏天炫腹的膀爷", "大爷男团请求出道", "当你跟妈妈要零花钱的时候", "肖战神仙颜值", "300斤女孩直播哗众取宠被抓", "彭于晏给洪金宝敬茶磕头","明星高能口误合集", "杨紫李现 甜", "这绝对是亲妈", "萍乡暴雨", "消防员救出小孩后哭了", "鹿晗 我没驼背", "邓紫棋把瓶盖踢回去", "终于找到理想的暑假工", "城管抱住小贩抢气球放飞", "刘德华拍戏用真刀"};
    public static final String[] Hotvalue= {"1144.9w", "861.1w", "841.8w", "820.2w", "796.9w", "776.8w", "754.5w", "737.0w", "720.5w", "699.5w","459.5w", "425.9w", "339.2w", "297.9w", "290.0w", "229.5w", "179.8w", "164.6w", "160.2w", "157.0w","146.4w", "78.6w", "76.2w", "65.4w", "49.0w", "47.7w", "41.4w", "39.8w", "37.3w", "32.6w"};

    public static final int DEFAULT_SIZE = 30;

    public static Data getSingleData(String info,String title,String Hotvalue) {
        return new Data(info,title,Hotvalue);
    }

    public static List<Data> getData() {
        return getData(DEFAULT_SIZE);
    }

    public static List<Data> getData(int size) {
        if (size > 30) {
            size = 30;
        }
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < size; ++ i) {
            Data data = new Data(No[i],TITLE[i],Hotvalue[i]);
            list.add(data);
        }
        return list;
    }
}
