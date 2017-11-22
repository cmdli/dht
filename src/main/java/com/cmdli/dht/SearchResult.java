
package com.cmdli.dht;

import java.util.List;

import com.cmdli.dht.Node;

public class SearchResult {
    public List<Node> nodes;
    public String value;
    public SearchResult(List<Node> nodes, String value) {
        this.nodes = nodes;
        this.value = value;
    }
}
