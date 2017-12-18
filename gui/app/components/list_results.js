import React from 'react';
import ReactTable from 'react-table';
import { connect } from 'react-redux';
import { selectBibRecord } from '../actions/global';
// Webpack will bundle the included styling
import 'react-table/react-table.css';

const columns = [{
    Header: 'Agency',
    accessor: 'agencyId'
},{
    Header: 'Record Id',
    accessor: 'bibliographicRecordId'
},{
    Header: 'Producer Version',
    accessor: 'producerVersion'
},{
    Header: 'Deleted',
    accessor: 'deleted',
    Cell: (props)=>""+props.value
},{
    Header: 'Tracking ID',
    accessor: 'trackingId'
}];

class ListResults extends React.PureComponent {
    constructor(props){
        super(props)
    }

    render(){
        return (
            <ReactTable
                loading={this.props.loading}
                columns={columns}
                data={this.props.results}
                getTrProps={(state,rowInfo,column) => {
                    return ({
                        onClick: (e,handleOriginal)=>{
                            this.props.selectItem(this.props.results[rowInfo.index]);
                            if(handleOriginal){
                                handleOriginal();
                            }
                        }
                    })
                }}
                showPaginagion={true}
                showPageSizeOptions={true}
                pageSizeOptions={[10, 20, 50, 100]}
                defaultPageSize={10}/>
        )
    }
}

const mapStateToProps = (state) => ({
    loading: state.search.searchPending,
    results: state.search.searchResults
});

const mapDispatchToProps = (dispatch) => ({
    selectItem: (item) => dispatch(selectBibRecord(item))
});

export default connect(mapStateToProps,mapDispatchToProps)(ListResults);