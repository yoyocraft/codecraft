import { listGeneratorVoByPageUsingPost } from '@/services/backend/generatorController';
import { UserOutlined } from '@ant-design/icons';
import { PageContainer, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import '@umijs/max';
import { Link } from '@umijs/max';
import { Avatar, Card, Flex, Input, List, message, Tabs, Tag, Typography } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';

/**
 * 默认分页参数
 */
const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 4,
  sortField: 'updateTime',
  sortOrder: 'descend',
};

const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>(DEFAULT_PAGE_PARAMS);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);
  const [total, setTotal] = useState<number>(0);

  /**
   * 搜索
   */
  const doSearch = async () => {
    setLoading(true);
    try {
      const res = await listGeneratorVoByPageUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    } catch (error: any) {
      message.error('获取数据失败, ' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    doSearch();
  }, [searchParams]);

  /**
   * 展示标签列表
   * @param tags 标签列表
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  return (
    <PageContainer title={<></>}>
      <Flex justify="center">
        <Input.Search
          allowClear
          enterButton="搜索"
          placeholder="搜索生成器"
          size="large"
          onChange={(e) => {
            searchParams.searchText = e.target.value;
          }}
          onSearch={(value: string) => {
            setSearchParams({
              ...searchParams,
              ...DEFAULT_PAGE_PARAMS,
              searchText: value,
            });
          }}
        />
      </Flex>

      <div style={{ marginBottom: 16 }} />

      <Tabs
        size="large"
        defaultActiveKey="newest"
        items={[
          {
            key: 'newest',
            label: '最新',
          },
          {
            key: 'recommand',
            label: '推荐',
          },
        ]}
        // TODO 完成推荐
        onChange={() => {}}
      />

      <QueryFilter
        span={12}
        labelWidth="auto"
        labelAlign="left"
        defaultCollapsed={false}
        style={{ padding: '16px 0' }}
        onFinish={async (values: API.GeneratorQueryRequest) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            // @ts-ignore
            ...values,
            searchText: searchParams.searchText,
          });
        }}
      >
        <ProFormSelect name="tags" label="标签" mode="tags" />
        <ProFormText name="name" label="名称" />
        <ProFormText name="description" label="描述" />
      </QueryFilter>

      <div style={{ marginBottom: 24 }} />

      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total: total,
          onChange(current: number, pageSize: number) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item>
            <Link to={`/generator/detail/${data.id}`}>
              <Card hoverable cover={<img alt={data.name} src={data.picture} />}>
                <Card.Meta
                  title={<a>{data.name}</a>}
                  description={
                    <Typography.Paragraph
                      ellipsis={{
                        rows: 2,
                      }}
                      style={{ height: 44 }}
                    >
                      {data.description}
                    </Typography.Paragraph>
                  }
                />
                {/* 展示标签 */}
                {tagListView(data.tags)}
                <Flex justify="space-between" align="center">
                  <Typography.Paragraph type="secondary" style={{ fontSize: 12 }}>
                    {moment(data.updateTime).fromNow()}
                  </Typography.Paragraph>
                  <div>
                    <Avatar src={data.user?.userAvatar ?? <UserOutlined />} />
                  </div>
                </Flex>
              </Card>
            </Link>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};
export default IndexPage;
