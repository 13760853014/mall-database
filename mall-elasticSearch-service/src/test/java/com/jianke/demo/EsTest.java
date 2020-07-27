package com.jianke.demo;

import com.jianke.demo.service.impl.RestHighLevelClientService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class EsTest {

    @Autowired
    private RestHighLevelClientService clientService;

    @Test
    public void createIdx() throws IOException {
        String settings = "" +
                "  {" +
                "      number_of_shards : 2," +
                "      number_of_replicas : 0" +
                "   }";
        String mappings = "" +
                "{" +
                "    properties: {" +
                "      itemId : {" +
                "        type: keyword," +
                "        ignore_above: 64" +
                "      }," +
                "      urlId : {" +
                "        type: keyword," +
                "        ignore_above: 64" +
                "      }," +
                "      sellAddress : {" +
                "        type: text," +
                "        analyzer: ik_max_word, " +
                "        search_analyzer: ik_smart," +
                "        fields: {" +
                "          keyword : {ignore_above : 256, type : keyword}" +
                "        }" +
                "      }," +
                "      courierFee : {" +
                "        type: text" +
                "      }," +
                "      promotions : {" +
                "        type: text," +
                "        analyzer: ik_max_word, " +
                "        search_analyzer: ik_smart," +
                "        fields: {" +
                "          keyword : {ignore_above : 256, type : keyword}" +
                "        }" +
                "      }," +
                "      originalPrice : {" +
                "        type: keyword," +
                "        ignore_above: 64" +
                "      }," +
                "      startTime : {" +
                "        type: date," +
                "        format: yyyy-MM-dd HH:mm:ss" +
                "      }," +
                "      endTime : {" +
                "        type: date," +
                "        format: yyyy-MM-dd HH:mm:ss" +
                "      }," +
                "      title : {" +
                "        type: text," +
                "        analyzer: ik_max_word, " +
                "        search_analyzer: ik_smart," +
                "        fields: {" +
                "          keyword : {ignore_above : 256, type : keyword}" +
                "        }" +
                "      }," +
                "      serviceGuarantee : {" +
                "        type: text," +
                "        analyzer: ik_max_word, " +
                "        search_analyzer: ik_smart," +
                "        fields: {" +
                "          keyword : {ignore_above : 256, type : keyword}" +
                "        }" +
                "      }," +
                "      venue : {" +
                "        type: text," +
                "        analyzer: ik_max_word, " +
                "        search_analyzer: ik_smart," +
                "        fields: {" +
                "          keyword : {ignore_above : 256, type : keyword}" +
                "        }" +
                "      }," +
                "      currentPrice : {" +
                "        type: keyword," +
                "        ignore_above: 64" +
                "      }" +
                "   }" +
                "}";
        clientService.createIndex("idx_item", settings, mappings);
    }


    @Test
    public void importAll() throws IOException {
        String itemJson = "{" +
                "    _id: https://detail.tmall.com/item.htm?id=538528948719\\u0026skuId=3216546934499," +
                "    卖家地址: 上海," +
                "    快递费: 运费: 0.00元," +
                "    优惠活动: 满199减10,满299减30,满499减60,可跨店," +
                "    商品ID: 538528948719," +
                "    原价: 2290.00," +
                "    活动开始时间: 2016-11-11 00:00:00," +
                "    活动结束时间: 2016-11-11 23:59:59," +
                "    标题: 【天猫海外直营】 ReFa CARAT RAY 黎珐 双球滚轮波光美容仪," +
                "    服务保障: 正品保证;赠运费险;极速退款;七天退换," +
                "    会场: 进口尖货," +
                "    现价: 1950.00" +
                "}";
        clientService.importAll("idx_item", true, itemJson);
    }

    @Test
    public void search() throws IOException {
        SearchResponse search = clientService.search("title", "酒", "currentPrice",
                "11", "149", "sellAddress", "武汉");
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println( documentFields.getSourceAsString());
        }
    }

}
