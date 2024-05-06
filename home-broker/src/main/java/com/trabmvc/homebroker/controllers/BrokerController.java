package com.trabmvc.homebroker.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trabmvc.homebroker.models.Stock;
import com.trabmvc.homebroker.services.BrokerService;
import com.trabmvc.homebroker.services.StockExchangeService;

@RestController
@RequestMapping("/broker")
@Validated
public class BrokerController {

    private BrokerService brokerService;
    private StockExchangeService stockExchangeService;

    @GetMapping("/broker/listen")
    public void listenForUpdates(@PathVariable String stockCode) {
        try {
            brokerService.listen(stockCode);
            stockExchangeService.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/broker/buy")
    public void buyStock(@RequestBody Stock stock) {
        brokerService.buyStock(stock);
    }

    @GetMapping("/broker/sell")
    public void sellStock(@RequestBody Stock stock) {
        brokerService.sellStock(stock);
    }

}
