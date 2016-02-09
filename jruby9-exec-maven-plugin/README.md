# jruby maven plugin

the plugin is modeled after [jruby-gradle](http://jruby-gradle.github.io/) and uses the old jruby maven plugins under the hood but it needs jruby-1.7.19 or newer (including jruby-9.0.0.0 serie).

even if the plugin depends on the old jruby-maven-plugins it has a different version.

## general command line switches

to see the java/jruby command the plugin is using (for example with the verify goal)

```mvn verify -Djruby.verbose```

to quickly pick another jruby version use

```mvn verify -Djruby.version=1.7.20```

to display some help

```mvn jruby9:help -Ddetail```

## jruby exec

it installs all the declared gems from the dependencies section as well the plugin dependencies. all jars are loaded with JRuby via ```require``` which loads them into the JRubyClassLoader.

the complete pom for the samples below is in [src/it/jrubyExecExample/pom.xml](src/it/jrubyExecExample/pom.xml)

the gem-artifacts are coming from the torquebox rubygems proxy

     <repositories>
       <repository>
         <id>mavengems</id>
         <url>mavengem:https://rubygems.org</url>
       </repository>
     </repositories>

     <build>
       <extensions>
         <extension>
           <groupId>org.torquebox.mojo</groupId>
           <artifactId>mavengem-wagon</artifactId>
           <version>0.2.0</version>
         </extension>
       </extensions>
    
to use these gems within the depenencies of the plugin you need

     <pluginRepositories>
       <pluginRepository>
         <id>mavengems</id>
         <url>mavengem:https://rubygems.org</url>
       </pluginRepository>
     </pluginRepositories>

the jar and gem artifacts for the JRuby application can be declared in the main dependencies section

    <dependencies>
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.6</version>
      </dependency>
      <dependency>
        <groupId>rubygems</groupId>
        <artifactId>leafy-rack</artifactId>
        <version>0.4.0</version>
        <type>gem</type>
      </dependency>
      <dependency>
        <groupId>rubygems</groupId>
        <artifactId>minitest</artifactId>
        <version>5.7.0</version>
        <type>gem</type>
        <scope>test</scope>
      </dependency>
    </dependencies>

these artifacts can have different scope BUT the exec goal will use ALL scopes.

the plugin declaration

    <build>
      <plugins>
        <plugin>
         <groupId>org.torquebox.mojo</groupId>
         <artifactId>jruby9-exec-maven-plugin</artifactId>
         <executions>
	       <execution>
             <id>simple</id>
             <phase>validate</phase>
	         <goals><goal>exec</goal></goals>
             <configuration>
               <jrubyVersion>9.0.3.0</jrubyVersion>
               <script>p 'hello world'</script>
             </configuration>
	       </execution>
	       <execution>
             <id>rspec</id>
             <phase>test</phase>
	         <goals><goal>exec</goal></goals>
             <configuration>
               <command>rspec</command>
             </configuration>
	       </execution>
	       <execution>
             <id>test file</id>
             <phase>test</phase>
	         <goals><goal>exec</goal></goals>
             <configuration>
               <file>test.rb</file>
             </configuration>
	       </execution>
	     </executions>

now the plugin uses rspec and needs rpsec gems installed via

        <dependencies>
          <dependency>
            <groupId>rubygems</groupId>
            <artifactId>rspec</artifactId>
            <version>3.3.0</version>
            <type>gem</type>
          </dependency>

the main dependencies section does use leafy-rack and to see some of its logging you need a slf4j logger for the tests

          <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.6</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>

## same config using the ruby DSL

[ruby DSL for maven](https://github.com/takari/polyglot-maven/tree/master/)

pom.rb

    extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
    repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'
    plugin_repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'
    
	jar 'org.slf4j', 'slf4j-api', '1.7.6'
	gem 'leafy-rack', '0.4.0'
	gem 'minitest', '5.7.0', :scope => :test

	plugin 'org.torquebox.mojo', 'jruby9-exec-maven-plugin' do
	  execute_goal :exec, :id => 'simple', :phase => :validate,
	    :jrubyVersion => '9.0.3.0', :script => "p 'hello world'"
      execute_goal :exec, :id => 'rspec', :phase => :test,
	    :command => :rspec
      execute_goal :exec, :id => 'test file', :phase => :test,
	    :file => 'test.rb'

	  gem 'rspec', '3.3.0'
	  jar 'org.slf4j', 'slf4j-simple', '1.7.6'
	end
