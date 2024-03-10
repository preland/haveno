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

package haveno.apitest.scenario.bot;

import haveno.apitest.method.BitcoinCliHelper;
import haveno.apitest.scenario.bot.protocol.BotProtocol;
import haveno.apitest.scenario.bot.protocol.MakerBotProtocol;
import haveno.apitest.scenario.bot.protocol.TakerBotProtocol;
import haveno.apitest.scenario.bot.script.BashScriptGenerator;
import haveno.apitest.scenario.bot.script.BotScript;
import haveno.apitest.scenario.bot.shutdown.ManualBotShutdownException;
import haveno.cli.table.builder.TableBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static haveno.apitest.scenario.bot.protocol.ProtocolStep.DONE;
import static haveno.apitest.scenario.bot.shutdown.ManualShutdown.isShutdownCalled;
import static haveno.cli.table.builder.TableType.BTC_BALANCE_TBL;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public
class RobotBob extends Bot {

    @Getter
    private int numTrades;

    public RobotBob(BotClient botClient,
                    BotScript botScript,
                    BitcoinCliHelper bitcoinCli,
                    BashScriptGenerator bashScriptGenerator) {
        super(botClient, botScript, bitcoinCli, bashScriptGenerator);
    }

    public void run() {
        for (String action : actions) {
            checkActionIsValid(action);

            BotProtocol botProtocol;
            if (action.equalsIgnoreCase(MAKE)) {
                botProtocol = new MakerBotProtocol(botClient,
                        paymentAccount,
                        protocolStepTimeLimitInMs,
                        bitcoinCli,
                        bashScriptGenerator);
            } else {
                botProtocol = new TakerBotProtocol(botClient,
                        paymentAccount,
                        protocolStepTimeLimitInMs,
                        bitcoinCli,
                        bashScriptGenerator);
            }

            botProtocol.run();

            if (!botProtocol.getCurrentProtocolStep().equals(DONE)) {
                throw new IllegalStateException(botProtocol.getClass().getSimpleName() + " failed to complete.");
            }

            StringBuilder balancesBuilder = new StringBuilder();
            balancesBuilder.append("BTC").append("\n");
            balancesBuilder.append(new TableBuilder(BTC_BALANCE_TBL, botClient.getBalance().getBtc()).build().toString()).append("\n");

            log.info("Completed {} successful trade{}.  Current Balance:\n{}",
                    ++numTrades,
                    numTrades == 1 ? "" : "s",
                    balancesBuilder);

            if (numTrades < actions.length) {
                try {
                    SECONDS.sleep(20);
                } catch (InterruptedException ignored) {
                    // empty
                }
            }

        } // end of actions loop

        if (stayAlive)
            waitForManualShutdown();
        else
            warnCLIUserBeforeShutdown();
    }

    private void checkActionIsValid(String action) {
        if (!action.equalsIgnoreCase(MAKE) && !action.equalsIgnoreCase(TAKE))
            throw new IllegalStateException(action + " is not a valid bot action; must be 'make' or 'take'");
    }

    private void waitForManualShutdown() {
        String harnessOrCase = isUsingTestHarness ? "harness" : "case";
        log.info("All script actions have been completed, but the test {} will stay alive"
                        + " until a /tmp/bottest-shutdown file is detected.",
                harnessOrCase);
        log.info("When ready to shutdown the test {}, run '$ touch /tmp/bottest-shutdown'.",
                harnessOrCase);
        if (!isUsingTestHarness) {
            log.warn("You will have to manually shutdown the bitcoind and Haveno nodes"
                    + " running outside of the test harness.");
        }
        try {
            while (!isShutdownCalled()) {
                SECONDS.sleep(10);
            }
            log.warn("Manual shutdown signal received.");
        } catch (ManualBotShutdownException ex) {
            log.warn(ex.getMessage());
        } catch (InterruptedException ignored) {
            // empty
        }
    }

    private void warnCLIUserBeforeShutdown() {
        if (isUsingTestHarness) {
            long delayInSeconds = 30;
            log.warn("All script actions have been completed.  You have {} seconds to complete any"
                            + " remaining tasks before the test harness shuts down.",
                    delayInSeconds);
            try {
                SECONDS.sleep(delayInSeconds);
            } catch (InterruptedException ignored) {
                // empty
            }
        } else {
            log.info("Shutting down test case");
        }
    }
}
