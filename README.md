# EasySwipeRefreshLayout

## 1.简介

Android下拉刷新控件，项目中主要包含两个module：

1. lib_easyswiperefreshlayout (下拉刷新独立lib，只包含下拉刷新相关逻辑）

2. app（demo样例怎么通过EasySwipeRefreshLayout自定义自己的下拉刷新控件）

## 2.效果展示

### 2.1 默认样式

![action1](/images/action1.gif)

### 2.2 HeaderView一起下滑样式

![action2](/images/action2.gif)

### 2.3 头部固定样式

![action3](/images/action3.gif)

### 目前就支持两种样式，后期持续更新...

## 3.代码说明

### 3.1 核心类：EasySwipeRefreshLayout

继承于ViewGroup，将第一个子View作为target(RecyclerView等)监听滑动状态，进而控制HeaderView的行为。
EasySwipeRefreshLayout做的工作主要是添加HeaderView，整体view的布局，以及向外传递滑动状态（做动画）。

自定义HeaderView用户主要关注以下3个方法：

1. **buildHeaderView**:子类重写，可自定义HeaderView

2. **onScrollStateChange**:向listener抛出当前下拉状态，header高度，下拉高度，用于header做动画或者逻辑控制。

3. **stopRefreshing**:结束刷新，清理动画

具体处理用户交互，滑动位置，嵌套滑动等都由EasySwipeRefreshLayout处理。开发者只需要关心header的部分。

### 3.2 自定义Header下拉样式：IStyleStrategy

通过抽象出策略接口，可以轻松扩展下拉的样式。目前支持:

1. FixedHeaderStrategy：固定位置的HeaderView

2. MoveHeaderStrategy：随着下拉一起移动的HeaderView

如果要扩展可以参考这两种实现。

### 3.3 实现方式

EasySwipeRefreshLayout的实现方式不同于以往通过监听 **touchEvent** 的方式，而通过监听 **NestedScroll**
的方式实现。具有以下优势：

1. 不需要重写 dispatchTouchEvent, onInterceptTouchEvent, onTouchEvent 控制时间分发，写复杂的逻辑控制。
虽然也能实现一样的效果，处理滑动冲突真实很费劲。

2. 很好的兼容NestedScroll组件，让滑动无比顺滑。

既然是使用 **NestedScroll** 实现，所以就有一个痛点问题，如果你放到EasySwipeRefreshLayout下的targetView不支持 nestedScroll，
那很显然就不支持了。那么对于这种情况怎么解决呢？

1. 对于常用的RecyclerView，这家伙天然支持 nestedScroll，不用担心。

2. 对于不支持nestedScroll的控件，比如：ListView，各种Layout等，你只需要在外包一层 NestedScrollView，解决所有问题。

3. 如果你觉得加一层 NestedScrollView有损性能，小弟无话可说，你就自己实现NestedScrollParent和NestedScrollingChild吧。

PS：NestedScrollView包裹ListView的时候，只能显示一个item的问题。请将ListView替换为lib中提供的NestedListView
具体原因请自行google。

## 4.最后

感谢你读到这里，本项目只是一个练手的项目，肯定还有很多不完善的地方，主要想解决平时在写下拉刷新的时候以及开源实现的一些问题，比如：

1. 可自定义HeaderView，但是不好自己做动画

2. 样式不容易扩展

3. 移植过程繁琐

4. 不够精简

项目中用到的所有图片和动画都来自网络，如果不慎侵犯了你的权益，请及时告知立马删除。
欢迎大家提issue以及帮忙改进项目，欢迎star和fork。持续更新中...
