/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package haveno.core.xmr.nodes;

import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import org.bitcoinj.core.PeerAddress;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmrNodesRepository {
    private final XmrNodeConverter converter;
    private final List<XmrNodes.XmrNode> nodes;

    public XmrNodesRepository(List<XmrNodes.XmrNode> nodes) {
        this(new XmrNodeConverter(), nodes);
    }

    public XmrNodesRepository(XmrNodeConverter converter, List<XmrNodes.XmrNode> nodes) {
        this.converter = converter;
        this.nodes = nodes;
    }

    public List<PeerAddress> getPeerAddresses(@Nullable Socks5Proxy proxy, boolean isUseClearNodesWithProxies) {
        List<PeerAddress> result;
        // We connect to onion nodes only in case we use Tor for BitcoinJ (default) to avoid privacy leaks at
        // exit nodes with bloom filters.
        if (proxy != null) {
            List<PeerAddress> onionHosts = getOnionHosts();
            result = new ArrayList<>(onionHosts);

            if (isUseClearNodesWithProxies) {
                // We also use the clear net nodes (used for monitor)
                List<PeerAddress> torAddresses = getClearNodesBehindProxy(proxy);
                result.addAll(torAddresses);
            }
        } else {
            result = getClearNodes();
        }
        return result;
    }

    private List<PeerAddress> getClearNodes() {
        return nodes.stream()
                .filter(XmrNodes.XmrNode::hasClearNetAddress)
                .flatMap(node -> nullableAsStream(converter.convertClearNode(node)))
                .collect(Collectors.toList());
    }

    private List<PeerAddress> getOnionHosts() {
        return nodes.stream()
                .filter(XmrNodes.XmrNode::hasOnionAddress)
                .flatMap(node -> nullableAsStream(converter.convertOnionHost(node)))
                .collect(Collectors.toList());
    }

    private List<PeerAddress> getClearNodesBehindProxy(Socks5Proxy proxy) {
        return nodes.stream()
                .filter(XmrNodes.XmrNode::hasClearNetAddress)
                .flatMap(node -> nullableAsStream(converter.convertWithTor(node, proxy)))
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> nullableAsStream(@Nullable T item) {
        return Optional.ofNullable(item).stream();
    }
}
