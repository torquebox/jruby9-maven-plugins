extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'
plugin_repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'
    
jar 'org.slf4j', 'slf4j-api', '1.7.6'
gem 'leafy-rack', '0.4.0'
gem 'minitest', '5.7.0'

resource :includes => ['test.rb', 'spec/**']

plugin :jar, '2.4' do
  execute_goal nil, :id => 'default-jar', :phase => :omit
end
plugin 'org.torquebox.mojo:jruby9-jar-maven-plugin' do
  execute_goals :generate, :process, :jar, :jrubyVersion => '${j.version}'
  
  gem 'rspec', '3.3.0'
  jar 'org.slf4j', 'slf4j-simple', '1.7.6'
end
