
package com.svoflee.spartacus.lib.dal.utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.svoflee.spartacus.core.utils.U;

/**
 * MybatisSourcesGenerator 是用于生成Mybatis相关代码的工具类
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class MybatisSourcesGenerator {

    // protected static final Logger log = Logger.getLogger(MybatisSourcesGenerator.class.getName());

    public static void main(String[] args) {
        String configFileName = ".//gen//mbgen//generatorConfig.xml";
        mybatisGenerate(configFileName);
    }

    /**
     * @param configFileName
     */
    public static void mybatisGenerate(String configFileName) {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File(configFileName);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        try {
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        }
        catch (IOException e) {
            U.p(e);
        }
        catch (XMLParserException e) {
            U.p(e);
        }
        catch (InvalidConfigurationException e) {
            U.p(e);
        }
        catch (SQLException e) {
            U.p(e);
        }
        catch (InterruptedException e) {
            U.p(e);
        }
        U.p("生成完毕...");

    }
}
