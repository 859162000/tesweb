package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.ICaseStatisticsService;
import com.dc.tes.ui.client.ICaseStatisticsServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTCaseRunStatistics;
import com.dc.tes.ui.client.model.GWTCaseRunUserStats;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CaseRunUserStatsPage extends BasePage {
			
		private GWTCaseRunStatistics gwtCaseRunStatistics;
		private boolean isFactorChange;
		private ICaseStatisticsServiceAsync staticticsService = null;
		/**
		 * 列表控件
		 */
		GridContentPanel<GWTCaseRunUserStats> panel;
		
		/**
		 * 详细信息控件
		 */
		FormContentPanel<GWTCaseRunUserStats> detailPanel;
		/**
		 * 工具条
		 */
		ConfigToolBar bottomBar;
		
		public CaseRunUserStatsPage(GWTCaseRunStatistics statistics, boolean isFactorChange){
			this.gwtCaseRunStatistics = statistics;
			this.isFactorChange = isFactorChange;
		}
		
		@Override
		protected void onRender(Element parent, int index) {
			super.onRender(parent, index);
			panel = new GridContentPanel<GWTCaseRunUserStats>();
			staticticsService = ServiceHelper.GetDynamicService("caseStatistics", ICaseStatisticsService.class);
			
			RpcProxy<PagingLoadResult<GWTCaseRunUserStats>> proxy = new RpcProxy<PagingLoadResult<GWTCaseRunUserStats>>() {
				@Override
				public void load(Object loadConfig,
						AsyncCallback<PagingLoadResult<GWTCaseRunUserStats>> callback) {
					staticticsService.getCaseRunUserStatList(gwtCaseRunStatistics, panel.GetSearchCondition(),
							(PagingLoadConfig) loadConfig, isFactorChange, callback);					
				}						
			};
			panel.setProxy(proxy);
			panel.setColumns(GetColumnConfig());
			panel.DrowGridView();
			panel.DrowSearchBar();
			bottomBar = new ConfigToolBar();
			bottomBar.initPageToolBar(panel.getLoader());		
			bottomBar.AddWidget(new FillToolItem());
			InitBtnConfigBar(bottomBar);
			panel.setBottomBar(bottomBar);
			add(panel);

			detailPanel = new FormContentPanel<GWTCaseRunUserStats>();
			detailPanel.setBindInfo(GetDetailHashMap());
			panel.setDetailForm(detailPanel);
			add(detailPanel);
		
		}


		private Map<String, String> GetDetailHashMap() {
			// TODO Auto-generated method stub
			Map<String, String> detailMap = new LinkedHashMap<String, String>();

			detailMap.put(GWTCaseRunUserStats.N_RunUserId, "用户");
			if(!isFactorChange){
				detailMap.put(GWTCaseRunUserStats.N_TotalRunCaseFlowCount, "执行用例总数");
				detailMap.put(GWTCaseRunUserStats.N_TotalPassedCaseFlowCount, "通过用例总数");
				detailMap.put(GWTCaseRunUserStats.N_CaseFlowPassRate, "用例通过率");
				detailMap.put(GWTCaseRunUserStats.N_TotalRunCaseCount, "执行案例总数");
			}else{
				detailMap.put(GWTCaseRunUserStats.N_CreatedTransactionCount, "新建交易个数");
				detailMap.put(GWTCaseRunUserStats.N_CreatedCaseFlowCount, "新建用例个数");
				detailMap.put(GWTCaseRunUserStats.N_CreatedCaseCount, "新建案例个数");
				detailMap.put(GWTCaseRunUserStats.N_CreatedSysParamCount, "新建参数个数");
				detailMap.put(GWTCaseRunUserStats.N_ModifiedTransactionCount, "修改交易个数");
				detailMap.put(GWTCaseRunUserStats.N_ModifiedCaseFlowCount, "修改用例个数");
				detailMap.put(GWTCaseRunUserStats.N_ModifiedCaseCount, "修改案例个数");
				detailMap.put(GWTCaseRunUserStats.N_ModifiedSysParamCount, "修改参数个数");
			}
			detailMap.put(GWTCaseRunStatistics.N_Memo, "备注");
			
			return detailMap;
		}


		private List<ColumnConfig> GetColumnConfig() {
			// TODO Auto-generated method stub
			List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

			columns.add(new ColumnConfig(GWTCaseRunUserStats.N_RunUserId, "用户", 100));
			if(!isFactorChange){
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_TotalRunCaseFlowCount, "执行用例总数", 120));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_TotalPassedCaseFlowCount, "通过用例总数", 120));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_CaseFlowPassRate, "用例通过率", 120));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_TotalRunCaseCount, "执行案例总数", 120));
			}else{
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_CreatedTransactionCount, "新建交易个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_CreatedCaseFlowCount, "新建用例个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_CreatedCaseCount, "新建案例个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_CreatedSysParamCount, "新建参数个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_ModifiedTransactionCount, "修改交易个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_ModifiedCaseFlowCount, "修改用例个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_ModifiedCaseCount, "修改案例个数", 80));
				columns.add(new ColumnConfig(GWTCaseRunUserStats.N_ModifiedSysParamCount, "修改参数个数", 80));
			}
			return columns;
		}
		
	}

