# jruby jar maven plugin

if either packs a ruby gems and ruby code into an archive so this
archive can be used by jruby when added to the classpath. or it packs a ruby application as runnable jar, i.e. all the ruby code and the gems and jars (which ruby loads via require) are packed inside the jar. the jar will include jruby-complete and jruby-mains to execute the ruby application via, i.e.

    java -jar my.jar -S rake

there is a more compact configuration using an maven extension: [jruby9-extensions](../jruby9-extensions)

## general command line switches

to see the java/jruby command the plugin is using (for example with the verify goal)

```mvn verify -Djruby.verbose```

to quickly pick another jruby version use

```mvn verify -Djruby.version=1.7.20```

or to display some help

```mvn jruby9-jar:help -Ddetail```
```mvn jruby9-jar:help -Ddetail -Dgoal=jar```

## jruby runnable jar

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
      </dependency>
    </dependencies>

these artifacts ALL have the default scope which gets packed into the jar.

adding ruby resources to your jar

    <build>
      <resources>
        <resource>
          <directory>${basedir}</directory>
          <includes>
            <include>test.rb</include>
            <include>spec/**</include>
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

and tell the jruby-jar mojo to pack the jar

      <plugin>
        <groupId>org.torquebox.mojo</groupId>
        <artifactId>jruby9-jar-maven-plugin</artifactId>
        <version>@project.version@</version>
        <configuration>
          <jrubyVersion>${j.version}</jrubyVersion>
        </configuration>
	    <executions>
	      <execution>
            <id>jruby-jar</id>
	        <goals>
              <goal>generate</goal>
              <goal>process</goal>
              <goal>jar</goal>
            </goals>
	      </execution>
	    </executions>

now the plugin does also pack the gems/jars declared inside the plugin sections

        <dependencies>
          <dependency>
            <groupId>rubygems</groupId>
            <artifactId>rspec</artifactId>
            <version>3.3.0</version>
            <type>gem</type>
          </dependency>

the main dependencies section does use leafy-rack and for its logging you need a slf4j logger.

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
    gem 'minitest', '5.7.0'

    resource :includes => ['test.rb', 'spec/**']

    plugin :jar, '2.4' do
      execute_goal nil, :id => 'default-jar', :phase => :omit
    end
    plugin 'org.torquebox.mojo', 'jruby9-jar-maven-plugin' do
      execute_goals [:generate, :process, :jar], :jrubyVersion => '${j.version}'

      gem 'rspec', '3.3.0'
      jar 'org.slf4j', 'slf4j-simple', '1.7.6'
    end


# jruby archive

the archive can have regular dependencies as any java jar artifact. to separate the java part from the jruby part, all dependencies for jruby needs to be declared inside the jruby9-jar-plugin section. the big difference is that the regular jar plugin does the packing and the there is no need for the 'jar' goal of the jruby-jar-maven-plugin (but the 'generate' and 'process' goals are needed).

pom.rb

    packaging :jar
    extension 'org.torquebox.mojo:mavengem-wagon:0.2.0'
    plugin_repository :id => :mavengems, :url => 'mavengem:https://rubygems.org'

    resource :includes => ['test.rb', 'spec/**']

    plugin 'org.torquebox.mojo', 'jruby9-jar-maven-plugin' do
      execute_goals [:generate, :process], :type => :archive

      jar 'org.slf4j', 'slf4j-api', '1.7.6'
      gem 'leafy-rack', '0.4.0'
      gem 'minitest', '5.7.0'
      jar 'org.slf4j', 'slf4j-simple', '1.7.6'
    end

# complete config

two examples with all possible config for the jruby9-jar-maven-plugin,
either using the plugin configuration itself or via properties
section. the properties have the advantage that you can overwrite the
config via the system properties with the commandline:

```
mvn package -Djruby.version=1.7.24
```

## using porperties

    properties( 'jruby.jar.type' => :runnable,
                'jruby.version' => '9.0.5.0',
                'jruby.mains.version' => '0.5.0' )
    plugin 'org.torquebox.mojo:jruby9-jar-maven-plugin', '0.3.1' do
      execute_goals [:generate, :process]
    end

## using the plugin configuration section

    plugin( 'org.torquebox.mojo:jruby9-jar-maven-plugin', '0.3.1'
            'type' => :archive,
	        'pluginDependenciesOnly' => true,
	        'jrubyVersion' => '9.1.0.0',
	        'jrubyMainsVersion' => '0.5.1' ) do
      execute_goals [:generate, :process, :jar]
    end

the ```pluginDependenciesOnly``` has default ```true``` for
archive-type ```archive``` and has default ```false``` for
archive-type ```runnable```. but can be set as needed.
