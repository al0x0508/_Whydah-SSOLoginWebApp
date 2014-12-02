package net.whydah.sso.notinuse.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandHelloWorldUser extends HystrixCommand<String> {

        private final String name;

        public CommandHelloWorldUser(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("SSOAuthGroup"));
            this.name = name;
        }

        @Override
        protected String run() {
            return "Hello " + name + "!";
        }
    }