package com.example;

import nablarch.fw.web.HttpRequest;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * 疎通確認用のアクションクラス。
 *
 * @deprecated TODO 疎通確認用のクラスです。確認完了後、削除してください。
 */
@Path("/find")
public class SampleAction {

    /**
     * 検索処理。
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param req HTTPリクエスト
     * @return ユーザ情報(JSON)
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Object findProducesJson(HttpRequest req) {
        return Map.of("message", "Hello World");
    }
}
