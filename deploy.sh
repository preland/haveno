#!/bin/bash

tmux new-session -s "deploy" -d "make monerod1-local"
tmux split-window -v "make monerod2-local"
tmux split-window -h "make seednode-local"
tmux split-window -v "make arbitrator-desktop-local"
tmux split-window -h "make user1-desktop-local"
tmux split-window -v "make user2-desktop-local"
tmux -2 attach-session -d
