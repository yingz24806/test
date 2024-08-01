package com.shopping;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.live.mysql.vos.AbcVo;
import com.live.mysql.vos.CccVo;
import com.live.mysql.vos.RestFullCurrencyVo;
import com.live.mysql.wallet.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ATest {

    public static void main(String[] args) {
        List<PaymentMethod> paymentMethodList = init();


        testa(paymentMethodList);

    }

    private static List<RestFullCurrencyVo> mapToRestFullCurrencyVos(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream()
                .map(paymentMethod -> {
                    RestFullCurrencyVo restFullCurrencyVo = new RestFullCurrencyVo();
                    BeanUtils.copyProperties(paymentMethod, restFullCurrencyVo);
                    return restFullCurrencyVo;
                })
                .collect(Collectors.toList());
    }

    private static CccVo mapToCccVo(Map.Entry<String, List<PaymentMethod>> currencyEntry) {
        CccVo cccVo = new CccVo();
        cccVo.setCurrency(currencyEntry.getKey());
        cccVo.getProtocolList().addAll(mapToRestFullCurrencyVos(currencyEntry.getValue()));
        return cccVo;
    }

    private static AbcVo mapToAbcVo(Map.Entry<String, List<PaymentMethod>> entry) {
        AbcVo abcVo = new AbcVo();
        abcVo.setTitle(entry.getKey());
        abcVo.getCccVoList().addAll(entry.getValue().stream()
                .collect(Collectors.groupingBy(PaymentMethod::getCurrency))
                .entrySet().stream()
                .map(ATest::mapToCccVo)
                .collect(Collectors.toList()));
        return abcVo;
    }

    private static void testa(List<PaymentMethod> paymentMethodList) {
        Map<String, List<PaymentMethod>> paymentMethodMap = paymentMethodList.stream()
                .collect(Collectors.groupingBy(PaymentMethod::getTitle));

        List<AbcVo> abcVos = paymentMethodMap.entrySet().stream()
                .map(ATest::mapToAbcVo)
                .collect(Collectors.toList());

        log.info("===1111111=====:{}", JSON.toJSONString(abcVos));
    }

}
