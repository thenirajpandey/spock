
```
<project name="HelloSpock" basedir="." xmlns:artifact="urn:maven-artifact-ant">
  <property name="src.dir" location="src" />
  <property name="build.dir" location="build" />
  <property name="lib.dir" location="lib" />
  
  <property name="maven.ant.tasks.url" value="http://www.apache.org/dist/maven/binaries/maven-ant-tasks-2.1.3.jar" />
  <property name="maven.ant.tasks.jar" value="${lib.dir}${file.separator}maven-ant-tasks-2.1.3.jar" />
  <available property="maven.ant.tasks.jar.exists" file="${maven.ant.tasks.jar}" />
	  
  <target name="bootstrap.maven.tasks" unless="maven.ant.tasks.jar.exists">
    <mkdir dir="${lib.dir}" />
    <get
      src="${maven.ant.tasks.url}"
      dest="${maven.ant.tasks.jar}" />
  </target>

  <target name="init.maven.tasks" depends="bootstrap.maven.tasks">
    <typedef 
      resource="org/apache/maven/artifact/ant/antlib.xml" 
      uri="urn:maven-artifact-ant" 
      classpath="${maven.ant.tasks.jar}" /> 
  </target>
  
  <target name="resolve.dependencies" depends="init.maven.tasks">   
    <artifact:dependencies pathId="classpath.spock">
      <dependency
        groupId="org.spockframework"
        artifactId="spock-core"
        version="0.7-groovy-2.0" />    
      <remoteRepository
        id="maven-central"
        url="https://repo1.maven.org/maven2/" />
      <remoteRepository
        id="spock-snapshots"
        url="https://oss.sonatype.org/content/repositories/snapshots/" />
    </artifact:dependencies>
  </target>
  
  <target name="init.groovy.tasks" depends="resolve.dependencies">
    <taskdef
      name="groovyc"
      classname="org.codehaus.groovy.ant.Groovyc"
      classpathref="classpath.spock" />  
  </target>

  <target name="init" depends="init.groovy.tasks">
    <tstamp />
    <mkdir dir="${build.dir}" />  
  </target> 

  <target name="compile" depends="init">
    <groovyc
      srcdir="${src.dir}"
     destdir="${build.dir}"
     classpathref="classpath.spock" />
  </target>
  
  <target name="test" depends="compile">
    <junit>
      <formatter type="plain" usefile="false" />
      <classpath path="${build.dir}" />
      <classpath refid="classpath.spock" />	  
      <test name="HelloSpock" />
    </junit>
  </target>

  <target name="clean">
    <delete dir="${build.dir}" />
  </target>
</project>
```