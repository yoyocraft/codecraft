import { GithubOutlined, LinkOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'youyichannel';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'Blog',
          title: (
            <>
              <LinkOutlined /> youyiのBlog
            </>
          ),
          href: 'https://codejuzi.icu',
          blankTarget: true,
        },
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined /> Github
            </>
          ),
          href: 'https://github.com/dingxinliang88',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
