# jruby extensions

it contains two extensions each with its own package type: jrubyJar
and jrubyWar

the extension uses the mojos from
[jruby9-jar-maven-plugin](../jruby9-jar-maven-plugin) and 
[jruby9-war-maven-plugin](../jruby9-war-maven-plugin)

## general command line switches

to see the java/jruby command the plugin is using (for example with the verify goal)

```mvn verify -Djruby.verbose```

to quickly pick another jruby version use

```mvn verify -Djruby.version=1.7.20```

or to display some help

```mvn jruby9-war:help -Ddetail```
```mvn jruby9-jar:help -Ddetail -Dgoal=generate```

## jruby runnable jar

it installs all the declared gems and jars from the dependencies section as well the plugin dependencies.

the complete pom for the samples below is in [src/it/jrubyJarExample/pom.xml](src/it/jrubyJarExample/pom.xml) and more details on how it can be executed.

for simplicity the ruby-dsl for maven is used.

	extension 'org.torquebox.mojo:jruby9-extensions:0.3.0'
    packaging :jrubyJar
    
    extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
    repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'

    properties 'jruby.version' => '1.7.21'

	jar 'org.slf4j', 'slf4j-api', '1.7.6'
	gem 'leafy-rack', '0.4.0'
	gem 'minitest', '5.7.0'
    gem 'rspec', '3.3.0'
    jar 'org.slf4j', 'slf4j-simple', '1.7.6'

	resource :includes => ['test.rb', 'spec/**']

## jruby war

some configuration of the jruby9-war plugin is done via the properties.

	extension 'org.torquebox.mojo:jruby9-extensions:0.3.0'
    packaging :jrubyWar
    
    extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
    repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'

    properties 'jruby.version' => '1.7.21', 'jruby.war.type' => :jetty

	gem 'leafy-complete', '0.4.0'
	gem 'sinatra', '1.4.5'
    jar 'org.slf4j', 'slf4j-simple', '1.7.6'

    resource :includes => [ 'Gemfile*', 'config.ru', 'app/**' ]
