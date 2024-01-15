generate-all:
			clojure -Adev -X com.widdindustries.gen.gen.tempo/generate-all
test-cljs:
			clojure -Adev -X dev/tests-ci-shadow :compile-mode :release
test-clj:
			clojure -Adev -X dev/run-clj-tests
.PHONY: list
list:
		@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null | awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' | sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs
