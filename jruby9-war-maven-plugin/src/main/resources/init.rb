# partially copied monkey_patch.rb from jruby-mains project

module Bundler
  module Patch
    def clean_load_path
      # nothing to be done for JRuby
    end
  end
  module SharedHelpers
    def included(bundler)
      bundler.send :include, Patch
    end
  end
end
begin
  require 'bundler/shared_helpers'
rescue LoadError
  # ignore and assume we do not use bundler
end
