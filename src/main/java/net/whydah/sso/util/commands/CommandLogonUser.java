package net.whydah.sso.util.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandLogonUser extends HystrixCommand<String> {

        private final String name;

        public CommandLogonUser(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("SSOAuthGroup"));
            this.name = name;
        }

        @Override
        protected String run() {
            return "Hello " + name + "!";
        }
    }