package com.github.rd4j;

public class Handler {

		public Resolution handle() {
			return new DummyResolution();
		}

		public Resolution showErrors(ErrorCollection err) {
			return null;
		}

		public Resolution withParam(@Required String name, @Required int value) {
			return new DummyResolution();
		}
		
}
