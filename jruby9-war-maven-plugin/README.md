# jruby war maven plugin

it packs a ruby application as runnable war, i.e. all the ruby code
and the gems and jars (which ruby loads via require) are packed inside
the war along with jruby-complete.jar and jruby-rack.jar. it also
includes a web.xml which configures the rack application to be found
in WEB-ING/classes. it also can embed jetty with which the application
can be executed like this

    java -jar my.war

or execute the jruby application by passing on the arguements:

java -jar my.war -S rake

there is also a more compact configuration using an maven extensions: [jruby9-extensions](../jruby9-extensions)

## general command line switches

to see the java/jruby command the plugin is using (for example with the verify goal)

```mvn verify -Djruby.verbose```

to quickly pick another jruby version use

```mvn verify -Djruby.version=1.7.20```

or to display some help

```mvn jruby9-war:help -Ddetail```
```mvn jruby9-war:help -Ddetail -Dgoal=jar```

## jruby war

it installs all the declared gems and jars from the dependencies section as well the plugin dependencies.

the complete pom for the samples below is in [src/it/jrubyJarExample/pom.xml](src/it/jrubyJarExample/pom.xml) and more details on how it can be executed.

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

the jar and gem artifacts for the JRuby application can be declared in the main dependencies section

    <dependencies>
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>1.7.6</version>
      </dependency>
      <dependency>
        <groupId>rubygems</groupId>
        <artifactId>leafy-complete</artifactId>
        <version>0.4.0</version>
        <type>gem</type>
      </dependency>
      <dependency>
        <groupId>rubygems</groupId>
        <artifactId>sinatra</artifactId>
        <version>1.4.5</version>
        <type>gem</type>
      </dependency>
    </dependencies>

these artifacts ALL have the default scope which gets packed into the jar.

adding ruby resources to your jar

    <build>
      <resources>
        <resource>
          <directory>${basedir}</directory>
          <includes>
            <include>config.ru</include>
            <include>app/**</include>
          </includes>
        </resource>
      </resources>

the plugin declarations. first we want to omit the regular jar packing

    <plugins>
      <plugin>
        <artifactId>maven-jar-plugin</artifactId>
        <version>2.4</version>
	    <executions>
	      <execution>
            <id>default-jar</id>
            <phase>omit</phase>
          </execution>
        </executions>
      </plugin>

the tell the jruby-war mojo to pack the war and embed jetty

      <plugin>
        <groupId>org.torquebox.mojo</groupId>
        <artifactId>jruby9-war-maven-plugin</artifactId>
        <version>@project.version@</version>
        <configuration>
          <type>jetty</type>
        </configuration>
	    <executions>
	      <execution>
            <id>jruby-war</id>
	        <goals>
              <goal>generate</goal>
              <goal>process</goal>
              <goal>war</goal>
            </goals>
	      </execution>
	    </executions>

or use type 'archive' for a plain war file without any embedded java.

## same config using the ruby DSL

[ruby DSL for maven](https://github.com/takari/polyglot-maven/tree/master/)

pom.rb

    extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
    repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'
    
	jar 'org.slf4j', 'slf4j-simple', '1.7.6'
	gem 'leafy-complete', '0.4.0'
	gem 'sinatra', '1.4.5'

	resource :includes => ['config.ru', 'app/**']

	plugin :jar, '2.4' do
	  execute_goal nil, :id => 'default-jar', :phase => :omit
	end
	plugin 'org.torquebox.mojo', 'jruby9-war-maven-plugin' do
	  execute_goals [generate, :process, :war, :type => :jetty
	end
